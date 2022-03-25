package nl.han.ica.icss.checker;

import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.Declaration;
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
import nl.han.ica.icss.ast.selectors.TagSelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheckerTest {

	private Checker sut;

	private AST ast;

	@BeforeEach
	void setUp() {
		sut = new Checker();
		ast = new AST();
	}

	@Test
	void variableReference_declaredAndInScope_noError() {
		ast.root.addChild(new VariableAssignment()
			                  .addChild(new VariableReference("Var"))
			                  .addChild(new PixelLiteral("100px"))
		);
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("width").addChild(new VariableReference("Var")))
		);

		sut.check(ast);

		assertTrue(ast.getErrors().isEmpty());
	}

	@Test
	void variableReference_undeclared_setsError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("width").addChild(new VariableReference("Var")))
		);

		sut.check(ast);

		assertTrue(hasError(ast, Checker.Error.VARIABLE_UNDECLARED));
	}

	@Test
	void variableReference_outOfScope_setsError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("a"))
			               .addChild(new VariableAssignment()
				                         .addChild(new VariableReference("Var"))
				                         .addChild(new PixelLiteral("100px")))
		);
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("width").addChild(new VariableReference("Var")))
		);

		sut.check(ast);

		assertTrue(hasError(ast, Checker.Error.VARIABLE_OUT_OF_SCOPE));
	}

	@Test
	void operation_allowedOperands_noError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("width")
				                         .addChild(new AddOperation()
					                                   .addChild(new PixelLiteral("2px"))
					                                   .addChild(new PixelLiteral("1px"))))
		);

		sut.check(ast);

		assertTrue(ast.getErrors().isEmpty());
	}

	@Test
	void operation_disallowedOperands_setsError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("width")
				                         .addChild(new AddOperation()
					                                   .addChild(new BoolLiteral("TRUE"))
					                                   .addChild(new PixelLiteral("1px"))))
		);

		sut.check(ast);

		assertTrue(hasError(ast, Checker.Error.OPERAND_DISALLOWED_TYPE));
	}

	@Test
	void operation_addOperationWithDifferentTypes_setsError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("width")
				                         .addChild(new AddOperation()
					                                   .addChild(new PixelLiteral("2px"))
					                                   .addChild(new ScalarLiteral("1"))))
		);

		sut.check(ast);

		hasError(ast, Checker.Error.ADD_SUBTRACT_DIFFERENT_TYPES);
	}

	// Test for AddOperation is covered by `operation_allowedOperands_noError()`
	@Test
	void operation_subtractOperationWithSameTypes_noError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("width")
				                         .addChild(new SubtractOperation()
					                                   .addChild(new PixelLiteral("2px"))
					                                   .addChild(new PixelLiteral("1px"))))
		);

		sut.check(ast);

		assertTrue(ast.getErrors().isEmpty());
	}

	@Test
	void operation_subtractOperationWithDifferentTypes_setsError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("width")
				                         .addChild(new SubtractOperation()
					                                   .addChild(new PixelLiteral("2px"))
					                                   .addChild(new ScalarLiteral("1"))))
		);

		sut.check(ast);

		assertTrue(hasError(ast, Checker.Error.ADD_SUBTRACT_DIFFERENT_TYPES));
	}

	@Test
	void operation_multiplyWithScalarType_noError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("width")
				                         .addChild(new MultiplyOperation()
					                                   .addChild(new PixelLiteral("2px"))
					                                   .addChild(new ScalarLiteral("1"))))
		);

		sut.check(ast);

		assertTrue(ast.getErrors().isEmpty());
	}

	@Test
	void operation_multiplyWithSameType_setsError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("width")
				                         .addChild(new MultiplyOperation()
					                                   .addChild(new PixelLiteral("2px"))
					                                   .addChild(new PixelLiteral("1px"))))
		);

		sut.check(ast);

		assertTrue(hasError(ast, Checker.Error.MULTIPLY_DISALLOWED_TYPE));
	}

	@Test
	void operation_multiplyWithDifferentNonScalarType_setsError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("width")
				                         .addChild(new MultiplyOperation()
					                                   .addChild(new PixelLiteral("2px"))
					                                   .addChild(new PercentageLiteral("1%"))))
		);

		sut.check(ast);

		assertTrue(hasError(ast, Checker.Error.MULTIPLY_DISALLOWED_TYPE));
	}

	@Test
	void mixin_notDeclaredYet_noError() {
		ast.root.addChild(new Mixin("Test"));
		sut.check(ast);
		assertTrue(ast.getErrors().isEmpty());
	}

	@Test
	void mixin_redeclared_setsError() {
		ast.root.addChild(new Mixin("Test")).addChild(new Mixin("Test"));
		sut.check(ast);
		assertTrue(hasError(ast, Checker.Error.MIXIN_REDECLARED));
	}

	@Test
	void declaration_allowedType_noError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("width").addChild(new PixelLiteral("20px")))
		);

		sut.check(ast);

		assertTrue(ast.getErrors().isEmpty());
	}

	@Test
	void declaration_unknownType_setsError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("unknown-declaration").addChild(new PixelLiteral("20px")))
		);

		sut.check(ast);

		assertTrue(hasError(ast, Checker.Error.DECLARATION_UNKNOWN_TYPE));
	}

	@Test
	void declaration_disallowedType_setsError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("width").addChild(new ColorLiteral("#ffffff")))
		);

		sut.check(ast);

		assertTrue(hasError(ast, Checker.Error.DECLARATION_DISALLOWED_TYPE));
	}

	@Test
	void ifClause_allowedExpressionType_noError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new IfClause()
				                         .addChild(new BoolLiteral("TRUE"))
				                         .addChild(new Declaration("width").addChild(new PixelLiteral("1px"))))
		);

		sut.check(ast);

		assertTrue(ast.getErrors().isEmpty());
	}

	@Test
	void ifClause_disallowedExpressionType_setsError() {
		ast.root.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new IfClause()
				                         .addChild(new ColorLiteral("#ffffff"))
				                         .addChild(new Declaration("width").addChild(new PixelLiteral("1px"))))
		);

		sut.check(ast);

		assertTrue(hasError(ast, Checker.Error.IF_CLAUSE_DISALLOWED_EXPRESSION_TYPE));
	}

	@Test
	void mixinCall_undeclaredMixin_setsError() {
		ast.root.addChild(new Stylerule()
			                  .addChild(new TagSelector("p"))
			                  .addChild(new MixinCall("Test"))
		);

		sut.check(ast);

		assertTrue(hasError(ast, Checker.Error.MIXIN_CALL_UNDECLARED_MIXIN));
	}

	private boolean hasError(AST ast, Checker.Error error) {
		// Can't use `contains()` as SemanticError doesn't override `equals()`
		return ast.getErrors().stream().anyMatch(e -> error.toString().equals(e.description));
	}

	@Nested
	@DisplayName("mixinCall_declaredMixin")
	class MixinCallDeclaredMixinTest {

		@BeforeEach
		void setUp() {
			ast.root
				.addChild(new Mixin("NestedTest")
					          .addChild(new VariableAssignment().addChild(new VariableReference("FgColor")))
					          .addChild(new Declaration("color").addChild(new VariableReference("FgColor"))))
				.addChild(new Mixin("Test")
					          .addChild(new VariableAssignment().addChild(new VariableReference("Size")))
					          .addChild(new VariableAssignment()
						                    .addChild(new VariableReference("Color"))
						                    .addChild(new ColorLiteral("#ffffff")))
					          .addChild(new Declaration("width").addChild(new VariableReference("Size")))
					          .addChild(new MixinCall("NestedTest").addChild(new VariableReference("Color")))
				);
		}

		@Test
		void allRequiredArgumentsGiven_noError() {
			ast.root.addChild(new Stylerule()
				                  .addChild(new TagSelector("p"))
				                  .addChild(new MixinCall("Test").addChild(new PixelLiteral("1px")))
			);

			sut.check(ast);

			assertTrue(ast.getErrors().isEmpty());
		}

		@Test
		void allRequiredAndOptionalArgumentsGiven_noError() {
			ast.root.addChild(new Stylerule()
				                  .addChild(new TagSelector("p"))
				                  .addChild(new MixinCall("Test")
					                            .addChild(new PixelLiteral("1px"))
					                            .addChild(new ColorLiteral("#123456")))
			);

			sut.check(ast);

			assertTrue(ast.getErrors().isEmpty());
		}

		@Test
		void tooManyArgumentsGiven_setsError() {
			ast.root.addChild(new Stylerule()
				                  .addChild(new TagSelector("p"))
				                  .addChild(new MixinCall("Test")
					                            .addChild(new PixelLiteral("1px"))
					                            .addChild(new ColorLiteral("#123456"))
					                            .addChild(new ColorLiteral("#123456")))
			);

			sut.check(ast);

			assertTrue(hasError(ast, Checker.Error.MIXIN_CALL_TOO_MANY_ARGUMENTS));
		}

		@Test
		void missingRequiredArguments_setsError() {
			ast.root.addChild(new Stylerule()
				                  .addChild(new TagSelector("p"))
				                  .addChild(new MixinCall("Test"))
			);

			sut.check(ast);

			assertTrue(hasError(ast, Checker.Error.MIXIN_CALL_MISSING_REQUIRED_ARGUMENTS));
		}

		@Test
		void hasNestedError_setsError() {
			ast.root.addChild(new Stylerule()
				                  .addChild(new TagSelector("p"))
				                  .addChild(new MixinCall("Test").addChild(new BoolLiteral("TRUE")))
			);

			sut.check(ast);

			assertTrue(hasError(ast, Checker.Error.MIXIN_CALL_NESTED_ERROR));
		}

		@Test
		void hasDeepNestedError() {
			ast.root.addChild(new Stylerule()
				                  .addChild(new TagSelector("p"))
				                  .addChild(new MixinCall("Test")
					                            .addChild(new PixelLiteral("1px"))
					                            .addChild(new BoolLiteral("TRUE")))
			);

			sut.check(ast);

			assertTrue(hasError(ast, Checker.Error.MIXIN_CALL_NESTED_ERROR));
		}

	}

}
