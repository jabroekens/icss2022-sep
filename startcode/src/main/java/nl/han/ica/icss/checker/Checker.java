package nl.han.ica.icss.checker;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.ElseClause;
import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.IfClause;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.Mixin;
import nl.han.ica.icss.ast.MixinCall;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

public class Checker {

	private static final EnumSet<ExpressionType> ALLOWED_OPERAND_TYPES = EnumSet.of(
		ExpressionType.PERCENTAGE, ExpressionType.PIXEL, ExpressionType.SCALAR
	);

	private static final Map<String, List<ExpressionType>> ALLOWED_DECLARATION_TYPES = Map.ofEntries(
		Map.entry("width", List.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL)),
		Map.entry("height", List.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL)),
		Map.entry("background-color", List.of(ExpressionType.COLOR)),
		Map.entry("color", List.of(ExpressionType.COLOR))
	);

	private IHANLinkedList<Map<String, ExpressionType>> variableTypes;
	private Map<String, Mixin> mixins;

	public void check(AST ast) {
		variableTypes = new HANLinkedList<>();
		mixins = new HashMap<>();

		stylesheet(ast.root);

		// Clean up to avoid memory leaks; we're not going to use them anymore
		variableTypes = null;
		mixins = null;
	}

	private void stylesheet(Stylesheet stylesheet) {
		var scopeIndex = increaseScope(-1);
		for (var node : stylesheet.body) {
			if (node instanceof VariableAssignment va) {
				variableAssignment(va, scopeIndex);
			} else if (node instanceof Mixin m) {
				mixin(m, scopeIndex);
			} else if (node instanceof Stylerule s) {
				stylerule(s, scopeIndex);
			}
		}
	}

	private void variableAssignment(VariableAssignment va, int scopeIndex) {
		/*
		 * Always traverse children first, otherwise a self-reference during
		 * assignment is not considered an error (which should of course be
		 * an error).
		 */
		expression(va.expression, scopeIndex);

		// A variable may have been declared in an outer scope, so we'll reassign it
		var foundIndex = scopeIndex;
		for (int i = 0; i <= scopeIndex; i++) {
			if (variableTypes.get(i).containsKey(va.name.name)) {
				foundIndex = i;
			}
		}

		variableTypes.get(foundIndex).put(va.name.name, getExpressionType(va.expression, scopeIndex));
	}

	private void expression(Expression expression, int scopeIndex) {
		if (expression instanceof VariableReference vr) {
			variableReference(vr, scopeIndex);
		} else if (expression instanceof Operation o) {
			operation(o, scopeIndex);
		}
	}

	private void variableReference(VariableReference vr, int scopeIndex) {
		var foundIndex = -1;
		for (var i = 0; i < variableTypes.getSize(); i++) {
			if (variableTypes.get(i).containsKey(vr.name)) {
				foundIndex = i;
				break;
			}
		}

		if (foundIndex < 0) {
			vr.setError(Error.VARIABLE_UNDECLARED.toString());
		} else if (foundIndex > scopeIndex) {
			vr.setError(Error.VARIABLE_OUT_OF_SCOPE.toString());
		}
	}

	private void operation(Operation o, int scopeIndex) {
		expression(o.lhs, scopeIndex);
		expression(o.rhs, scopeIndex);

		var lhsExpressionType = getExpressionType(o.lhs, scopeIndex);
		var rhsExpressionType = getExpressionType(o.rhs, scopeIndex);

		if (!ALLOWED_OPERAND_TYPES.contains(lhsExpressionType) || !ALLOWED_OPERAND_TYPES.contains(rhsExpressionType)) {
			o.setError(Error.OPERAND_DISALLOWED_TYPE.toString());
		} else if (o instanceof AddOperation || o instanceof SubtractOperation) {
			if (lhsExpressionType != rhsExpressionType) {
				o.setError(Error.ADD_SUBTRACT_DIFFERENT_TYPES.toString());
			}
		} else if (o instanceof MultiplyOperation) {
			if (!rhsExpressionType.equals(ExpressionType.SCALAR) &&
			    !lhsExpressionType.equals(ExpressionType.SCALAR)) {
				o.setError(Error.MULTIPLY_DISALLOWED_TYPE.toString());
			}
		}
	}

	private void mixin(Mixin m, int scopeIndex) {
		var oldMixin = mixins.put(m.name.name, m);
		if (oldMixin != null) {
			m.setError(Error.MIXIN_REDECLARED.toString());
			// Revert so we don't get false-positives when checking mixin calls
			mixins.put(m.name.name, oldMixin);
		}
	}

	private void stylerule(Stylerule s, int scopeIndex) {
		var newScopeIndex = increaseScope(scopeIndex);
		s.body.forEach(n -> styleruleBody(n, newScopeIndex));
	}

	private void styleruleBody(ASTNode node, int scopeIndex) {
		if (node instanceof VariableAssignment va) {
			variableAssignment(va, scopeIndex);
		} else if (node instanceof Declaration d) {
			declaration(d, scopeIndex);
		} else if (node instanceof IfClause c) {
			ifClause(c, scopeIndex);
		} else if (node instanceof ElseClause c) {
			elseClause(c, scopeIndex);
		} else if (node instanceof MixinCall mc) {
			mixinCall(mc, scopeIndex);
		} else if (node instanceof Stylerule s) {
			stylerule(s, scopeIndex);
		}
	}

	private void declaration(Declaration d, int scopeIndex) {
		expression(d.expression, scopeIndex);

		var allowedTypes = ALLOWED_DECLARATION_TYPES.get(d.property.name);
		if (allowedTypes == null) {
			d.setError(Error.DECLARATION_UNKNOWN_TYPE.toString());
		} else if (!allowedTypes.contains(getExpressionType(d.expression, scopeIndex))
		           && !hasNestedError(d.expression)) {
			/*
			 * We need to check for nested errors, because if there are nested
			 * errors, `getExpressionType()` will return UNDEFINED which in
			 * this case is a false-positive.
			 */
			d.setError(Error.DECLARATION_DISALLOWED_TYPE.toString());
		}
	}

	private void ifClause(IfClause i, int scopeIndex) {
		if (getExpressionType(i.conditionalExpression, scopeIndex) != ExpressionType.BOOL) {
			i.setError(Error.IF_CLAUSE_DISALLOWED_EXPRESSION_TYPE.toString());
		}

		var newScopeIndex = increaseScope(scopeIndex);
		i.body.forEach(n -> styleruleBody(n, newScopeIndex));
	}

	private void elseClause(ElseClause e, int scopeIndex) {
		var newScopeIndex = increaseScope(scopeIndex);
		e.body.forEach(n -> styleruleBody(n, newScopeIndex));
	}

	private void mixinCall(MixinCall mixinCall, int scopeIndex) {
		var mixin = mixins.get(mixinCall.name.name);
		if (mixin != null) {
			if (mixinCall.arguments.size() > mixin.arguments.size()) {
				mixinCall.setError(Error.MIXIN_CALL_TOO_MANY_ARGUMENTS.toString());
			} else if (mixinCall.arguments.size() < mixin.arguments.stream()
			                                                       .filter(va -> va.expression == null).count()) {
				mixinCall.setError(Error.MIXIN_CALL_MISSING_REQUIRED_ARGUMENTS.toString());
			} else {
				// Treat mixins like actual function calls; each execution has its own scope
				var newScopeIndex = increaseScope(scopeIndex);

				// Only load default values if value for default-valued argument has not been passed
				for (int i = 0, argc = mixinCall.arguments.size(); i < mixin.arguments.size(); i++) {
					var expr = i < argc ? mixinCall.arguments.get(i) : mixin.arguments.get(i).expression;

					variableTypes.get(newScopeIndex).put(
						mixin.arguments.get(i).name.name,
						getExpressionType(expr, newScopeIndex)
					);

					expression(expr, newScopeIndex);
				}

				var hasPreviousErrors = hasNestedError(mixin);
				mixin.body.forEach(n -> styleruleBody(n, newScopeIndex));

				if (hasNestedError(mixin) && !hasPreviousErrors) {
					mixinCall.setError(Error.MIXIN_CALL_NESTED_ERROR.toString());
				}
			}
		} else {
			mixinCall.setError(Error.MIXIN_CALL_UNDECLARED_MIXIN.toString());
		}
	}

	private int increaseScope(int origScopeIndex) {
		variableTypes.insert(++origScopeIndex, new HashMap<>());
		return origScopeIndex;
	}

	private boolean hasNestedError(ASTNode node) {
		if (node.getChildren().size() > 0) {
			for (var child : node.getChildren()) {
				if (hasNestedError(child)) {
					return true;
				}
			}
		}

		return node.hasError();
	}

	private ExpressionType getExpressionType(Expression expression, int scopeIndex) {
		if (expression instanceof VariableReference vr) {
			return getExpressionType(vr, scopeIndex);
		} else if (expression instanceof Operation o) {
			return getExpressionType(o, scopeIndex);
		} else if (expression instanceof Literal l) {
			return getExpressionType(l);
		}

		return ExpressionType.UNDEFINED;
	}

	private ExpressionType getExpressionType(VariableReference vr, int scopeIndex) {
		// Start at the most inner scope
		for (var i = scopeIndex; i >= 0; i--) {
			var l = variableTypes.get(i).get(vr.name);
			if (l != null) {
				return l;
			}
		}

		return ExpressionType.UNDEFINED;
	}

	private ExpressionType getExpressionType(Operation o, int scopeIndex) {
		var lhsExpressionType = getExpressionType(o.lhs, scopeIndex);
		var rhsExpressionType = getExpressionType(o.rhs, scopeIndex);
		return lhsExpressionType == ExpressionType.SCALAR ? rhsExpressionType : lhsExpressionType;
	}

	private ExpressionType getExpressionType(Literal literal) {
		if (literal instanceof ColorLiteral) {
			return ExpressionType.COLOR;
		} else if (literal instanceof PixelLiteral) {
			return ExpressionType.PIXEL;
		} else if (literal instanceof PercentageLiteral) {
			return ExpressionType.PERCENTAGE;
		} else if (literal instanceof ScalarLiteral) {
			return ExpressionType.SCALAR;
		} else if (literal instanceof BoolLiteral) {
			return ExpressionType.BOOL;
		}

		return ExpressionType.UNDEFINED;
	}

	protected enum Error {

		VARIABLE_UNDECLARED("Reference to undeclared variable"),

		VARIABLE_OUT_OF_SCOPE("Reference to out-of-scope variable"),

		OPERAND_DISALLOWED_TYPE("Operation operands can only be one of " + ALLOWED_OPERAND_TYPES + " types"),

		ADD_SUBTRACT_DIFFERENT_TYPES("Cannot add or subtract different types"),

		MULTIPLY_DISALLOWED_TYPE("Can only multiply with " + ExpressionType.SCALAR + " type"),

		MIXIN_REDECLARED("Cannot redeclare mixin"),

		DECLARATION_UNKNOWN_TYPE("Declaration type unknown"),

		DECLARATION_DISALLOWED_TYPE("Disallowed type for declaration"),

		IF_CLAUSE_DISALLOWED_EXPRESSION_TYPE("Expression in if-clause must be of type " + ExpressionType.BOOL),

		MIXIN_CALL_TOO_MANY_ARGUMENTS("Mixin call argument count exceeds mixin declaration argument count"),

		MIXIN_CALL_MISSING_REQUIRED_ARGUMENTS("Mixin call missing required arguments"),

		MIXIN_CALL_NESTED_ERROR("Error in mixin body as a result of mixin call"),

		MIXIN_CALL_UNDECLARED_MIXIN("Reference to undeclared mixin");

		private final String errorMsg;

		Error(String errorMsg) {
			this.errorMsg = errorMsg;
		}

		@Override
		public String toString() {
			return errorMsg;
		}
	}

}
