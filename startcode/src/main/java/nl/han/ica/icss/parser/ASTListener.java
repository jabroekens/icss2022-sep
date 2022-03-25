package nl.han.ica.icss.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.ElseClause;
import nl.han.ica.icss.ast.IfClause;
import nl.han.ica.icss.ast.Mixin;
import nl.han.ica.icss.ast.MixinCall;
import nl.han.ica.icss.ast.Stylerule;
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
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

	private final AST ast;
	private final IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}

	public AST getAST() {
		return ast;
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		var children = new ArrayList<ASTNode>();

		for (var i = 0; i < ctx.variableAssignment().size() + ctx.mixin().size() + ctx.stylerule().size(); i++) {
			children.add(currentContainer.pop());
		}

		for (var li = children.listIterator(children.size()); li.hasPrevious(); ) {
			ast.root.addChild(li.previous());
		}
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		var value = currentContainer.pop();
		var name = currentContainer.pop();
		currentContainer.push(new VariableAssignment().addChild(name).addChild(value));
	}

	@Override
	public void exitMixin(ICSSParser.MixinContext ctx) {
		var mixin = new Mixin();

		// +1 to include VariableReference (mixin name)
		for (var i = 0; i < 1 + ctx.mixinArgument().size() + ctx.styleruleBody().size(); i++) {
			mixin.addChild(currentContainer.pop());
		}

		Collections.reverse(mixin.arguments);
		Collections.reverse(mixin.body);
		currentContainer.push(mixin);
	}

	@Override
	public void exitMixinArgument(ICSSParser.MixinArgumentContext ctx) {
		ASTNode defaultValue = null;
		if (ctx.expression() != null) {
			defaultValue = currentContainer.pop();
		}

		var name = currentContainer.pop();

		var mixinArgument = new VariableAssignment().addChild(name);
		if (defaultValue != null) {
			mixinArgument.addChild(defaultValue);
		}

		currentContainer.push(mixinArgument);
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		var stylerule = new Stylerule();

		for (var i = 0; i < ctx.selector().size() + ctx.styleruleBody().size(); i++) {
			stylerule.addChild(currentContainer.pop());
		}

		Collections.reverse(stylerule.selectors);
		Collections.reverse(stylerule.body);
		currentContainer.push(stylerule);
	}

	@Override
	public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
		currentContainer.push(new TagSelector(ctx.LOWER_IDENT().getText()));
	}

	@Override
	public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
		currentContainer.push(new ClassSelector(ctx.CLASS_IDENT().getText()));
	}

	@Override
	public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
		currentContainer.push(new IdSelector(ctx.ID_IDENT().getText()));
	}

	@Override
	public void exitMixinCall(ICSSParser.MixinCallContext ctx) {
		var mixinCall = new MixinCall();

		/*
		 * We need to add the stack contents in reverse order to mixinCall, otherwise `mixinCall.name`
		 * gets set to the wrong value if an argument to the mixin call is itself a VariableReference
		 * (name gets set to the argument instead of the argument being added to the body).
		 * We can utilize `LinkedList#addFirst` for this.
		 */
		var children = new LinkedList<ASTNode>();

		// +1 to include `variableReference` (see parser rules)
		for (var i = 0; i < 1 + ctx.expression().size(); i++) {
			children.addFirst(currentContainer.pop());
		}

		children.forEach(mixinCall::addChild);
		currentContainer.push(mixinCall);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.push(new Declaration(ctx.LOWER_IDENT().getText()).addChild(currentContainer.pop()));
	}

	@Override
	public void exitIfClause(ICSSParser.IfClauseContext ctx) {
		var ifClause = new IfClause();

		// +1 to include `expression` (see parser rules)
		for (var i = 0; i < 1 + ctx.styleruleBody().size(); i++) {
			ifClause.addChild(currentContainer.pop());
		}

		Collections.reverse(ifClause.body);
		currentContainer.push(ifClause);
	}

	@Override
	public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
		var elseClause = new ElseClause();

		for (var i = 0; i < ctx.styleruleBody().size(); i++) {
			elseClause.addChild(currentContainer.pop());
		}

		Collections.reverse(elseClause.body);
		currentContainer.peek().addChild(elseClause);
	}

	/*
	 * Duplicate code here is a consequence of the decision to 'abstract' expressions into more
	 * specific 'parts'. Code duplication could be removed by checking the presence of a token
	 * (or a label in the case of multi-token operators/operands) in the context and creating
	 * instances of the relevant class based on it.
	 *
	 * The downside to the aforementioned approach is that every time you add a new expression
	 * to the parser rules, you'll have to modify the relevant method to check for the additional
	 * token(s)/label. By using labels for the expressions themselves, you simply only have to add
	 * a new method for that specific expression. This is more in line with the open-closed principle.
	 */

	@Override
	public void exitMultiplyOperation(ICSSParser.MultiplyOperationContext ctx) {
		var rhs = currentContainer.pop();
		var lhs = currentContainer.pop();
		currentContainer.push(new MultiplyOperation().addChild(lhs).addChild(rhs));
	}

	@Override
	public void exitAddOperation(ICSSParser.AddOperationContext ctx) {
		var rhs = currentContainer.pop();
		var lhs = currentContainer.pop();
		currentContainer.push(new AddOperation().addChild(lhs).addChild(rhs));
	}

	@Override
	public void exitSubtractOperation(ICSSParser.SubtractOperationContext ctx) {
		var rhs = currentContainer.pop();
		var lhs = currentContainer.pop();
		currentContainer.push(new SubtractOperation().addChild(lhs).addChild(rhs));
	}

	@Override
	public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		currentContainer.push(new ColorLiteral(ctx.getText()));
	}

	@Override
	public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		currentContainer.push(new PixelLiteral(ctx.getText()));
	}

	@Override
	public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		currentContainer.push(new PercentageLiteral(ctx.getText()));
	}

	@Override
	public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		currentContainer.push(new ScalarLiteral(ctx.getText()));
	}

	@Override
	public void exitBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
		currentContainer.push(new BoolLiteral(ctx.getText()));
	}

	@Override
	public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
		currentContainer.push(new VariableReference(ctx.getText()));
	}

}
