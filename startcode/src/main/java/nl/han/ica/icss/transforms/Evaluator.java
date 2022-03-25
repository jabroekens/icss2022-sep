package nl.han.ica.icss.transforms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
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
import nl.han.ica.icss.ast.Selector;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.NestedSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class Evaluator implements Transform {

	// Variables are evaluated the moment they're assigned, hence Literal instead of Expression
	private IHANLinkedList<Map<String, Literal>> variables;
	private Map<String, Mixin> mixins;

	@Override
	public void apply(AST ast) {
		variables = new HANLinkedList<>();
		mixins = new HashMap<>();

		stylesheet(ast.root);

		// Clean up to avoid memory leaks; we're not going to use them anymore
		variables = null;
		mixins = null;
	}

	private void stylesheet(Stylesheet stylesheet) {
		increaseScope();
		for (var li = stylesheet.body.listIterator(); li.hasNext(); ) {
			var node = li.next();
			li.remove();

			if (node instanceof VariableAssignment va) {
				variableAssignment(va);
			} else if (node instanceof Mixin m) {
				mixin(m);
			} else if (node instanceof Stylerule s) {
				stylerule(s).forEach(li::add);
			}
		}
		decreaseScope();
	}

	private void variableAssignment(VariableAssignment va) {
		// A variable may have been declared in an outer scope, so we'll reassign it
		var foundIndex = 0;
		for (int i = variables.getSize() - 1; i >= 0; i--) {
			if (variables.get(i).containsKey(va.name.name)) {
				foundIndex = i;
			}
		}

		variables.get(foundIndex).put(va.name.name, expression(va.expression));
	}

	private Literal expression(Expression expression) {
		if (expression instanceof VariableReference vr) {
			return variableReference(vr);
		} else if (expression instanceof Operation o) {
			return operation(o);
		} else if (expression instanceof Literal l) {
			return l;
		}

		return null;
	}

	private Literal variableReference(VariableReference vr) {
		for (var i = 0; i < variables.getSize(); i++) {
			var l = variables.get(i).get(vr.name);
			if (l != null) {
				return l;
			}
		}

		return null;
	}

	private Literal operation(Operation o) {
		var lhsLiteral = expression(o.lhs);
		var rhsLiteral = expression(o.rhs);

		// XXX make more OCP-friendly (delegate operation result type 'checking' (i.e. which type it should return)?)
		/*
		 * Unfortunately, the existing code for the Literal/Operation classes doesn't make good use
		 * of abstraction, so polymorphism isn't feasibly possible. Ideally, we'd be able to call
		 * `calc()` on the operation classes themselves and/or `NumberedLiteral.value` where
		 * `NumberedLiteral` is an abstract subclass of Literal extended by PixelLiteral,
		 * PercentageLiteral and ScalarLiteral.
		 */
		BiFunction<Integer, Integer, Integer> operation = (i, j) -> (i * j);
		if (o instanceof AddOperation) {
			operation = Integer::sum;
		} else if (o instanceof SubtractOperation) {
			operation = (i, j) -> (i - j);
		}

		if (lhsLiteral instanceof PixelLiteral ll) {
			if (rhsLiteral instanceof PixelLiteral rl) {
				return new PixelLiteral(operation.apply(ll.value, rl.value));
			} else if (rhsLiteral instanceof ScalarLiteral rl) {
				return new PixelLiteral(operation.apply(ll.value, rl.value));
			}
		} else if (lhsLiteral instanceof PercentageLiteral ll) {
			if (rhsLiteral instanceof PercentageLiteral rl) {
				return new PercentageLiteral(operation.apply(ll.value, rl.value));
			} else if (rhsLiteral instanceof ScalarLiteral rl) {
				return new PercentageLiteral(operation.apply(ll.value, rl.value));
			}
		} else if (rhsLiteral instanceof PixelLiteral ll) {
			if (lhsLiteral instanceof ScalarLiteral rl) {
				return new PixelLiteral(operation.apply(ll.value, rl.value));
			}
		} else if (rhsLiteral instanceof PercentageLiteral ll) {
			if (lhsLiteral instanceof ScalarLiteral rl) {
				return new PercentageLiteral(operation.apply(ll.value, rl.value));
			}
		}
		return null;
	}

	private void mixin(Mixin m) {
		mixins.put(m.name.name, m);
	}

	private List<ASTNode> stylerule(Stylerule stylerule) {
		increaseScope();
		// Cannot be of IHANLinkedList because it doesn't extend List
		var result = new LinkedList<ASTNode>();

		for (var li = stylerule.body.listIterator(); li.hasNext(); ) {
			var node = li.next();
			li.remove();

			for (var n : styleruleBody(node)) {
				if (n instanceof Stylerule childStylerule) {
					for (var sli = childStylerule.selectors.listIterator(); sli.hasNext(); ) {
						var childSelector = sli.next();
						sli.remove();
						for (var parentSelector : stylerule.selectors) {
							var nestedSelector = new NestedSelector(parentSelector, childSelector);
							sli.add(nestedSelector);
						}
					}

					result.add(n);
				} else {
					li.add(n);
				}
			}
		}

		decreaseScope();
		result.addFirst(stylerule);
		return result;
	}

	private List<ASTNode> styleruleBody(ASTNode node) {
		if (node instanceof VariableAssignment va) {
			variableAssignment(va);
		} else if (node instanceof Declaration d) {
			return List.of(declaration(d));
		} else if (node instanceof IfClause i) {
			return ifClause(i);
		} else if (node instanceof MixinCall mc) {
			return mixinCall(mc);
		} else if (node instanceof Stylerule s) {
			return stylerule(s);
		}

		return List.of();
	}

	private ASTNode declaration(Declaration d) {
		d.expression = expression(d.expression);
		return d;
	}

	private List<ASTNode> ifClause(IfClause ifClause) {
		increaseScope();

		if (expression(ifClause.conditionalExpression) instanceof BoolLiteral b && !b.value) {
			if (ifClause.elseClause != null) {
				ifClause.body = ifClause.elseClause.body;
			} else {
				return List.of();
			}
		}

		for (var li = ifClause.body.listIterator(); li.hasNext(); ) {
			var node = li.next();
			li.remove();
			styleruleBody(node).forEach(li::add);
		}

		decreaseScope();
		return ifClause.body;
	}

	private List<ASTNode> mixinCall(MixinCall mixinCall) {
		var mixin = mixins.get(mixinCall.name.name);
		var body = new ArrayList<ASTNode>();

		if (mixin != null) {
			// Treat mixins like actual function calls; each execution has its own scope
			increaseScope();
			// Only load default values if value for default-valued argument has not been passed
			for (int i = 0, argc = mixinCall.arguments.size(); i < mixin.arguments.size(); i++) {
				variables.getFirst().put(
					mixin.arguments.get(i).name.name,
					expression(i < argc ? mixinCall.arguments.get(i) : mixin.arguments.get(i).expression)
				);
			}

			for (var node : mixin.body) {
				/*
				 * We need to 'deep-copy' the mixin body, otherwise when the same mixin is called multiple times,
				 * it results in the wrong values. This is because some nodes get modified during evaluation.
				 */
				body.addAll(styleruleBody(deepCopy(node)));
			}
			decreaseScope();
		}

		return body;
	}

	// Helper functions so the code reads more clearly
	private void increaseScope() {
		variables.addFirst(new HashMap<>());
	}

	private void decreaseScope() {
		variables.removeFirst();
	}

	private ASTNode deepCopy(ASTNode node) {
		// All these node types get modified, so we must copy them
		if (node instanceof Stylerule s) {
			var copy = new Stylerule();
			copy.selectors = new ArrayList<>(s.selectors.size());
			s.selectors.forEach(n -> copy.selectors.add((Selector) deepCopy(n)));

			if (copy.body != null) {
				copy.body = new ArrayList<>(s.body.size());
				s.body.forEach(n -> copy.body.add(deepCopy(n)));
			}

			return copy;
		} else if (node instanceof Declaration d) {
			var copy = new Declaration();
			copy.property = d.property;
			copy.expression = d.expression;
			return copy;
		} else if (node instanceof IfClause c) {
			var copy = new IfClause(c.conditionalExpression, new ArrayList<>(c.body.size()));
			c.body.forEach(n -> copy.body.add(deepCopy(n)));

			if (c.elseClause != null) {
				copy.elseClause = new ElseClause(new ArrayList<>(c.elseClause.body.size()));
				c.elseClause.body.forEach(n -> copy.elseClause.body.add(deepCopy(n)));
			}

			return copy;
		} else if (node instanceof TagSelector s) {
			return new TagSelector(s.tag);
		} else if (node instanceof ClassSelector s) {
			return new ClassSelector(s.cls);
		} else if (node instanceof IdSelector s) {
			return new IdSelector(s.id);
		}

		return node;
	}

}
