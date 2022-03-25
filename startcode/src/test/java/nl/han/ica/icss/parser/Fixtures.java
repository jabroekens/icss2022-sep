package nl.han.ica.icss.parser;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class Fixtures {

    public static AST uncheckedLevel0() {
		Stylesheet stylesheet = new Stylesheet();
		/*
		p {
			background-color: #ffffff;
			width: 500px;
		}
		*/
		stylesheet.addChild((new Stylerule())
				.addChild(new TagSelector("p"))
				.addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
				.addChild((new Declaration("width"))
						.addChild(new PixelLiteral("500px")))
		);
		/*
		a {
			color: #ff0000;
		}
		*/
		stylesheet.addChild((new Stylerule())
				.addChild(new TagSelector("a"))
				.addChild((new Declaration("color"))
						.addChild(new ColorLiteral("#ff0000")))
		);
		/*
		#menu {
			width: 520px;
		}
		*/
		stylesheet.addChild((new Stylerule())
				.addChild(new IdSelector("#menu"))
				.addChild((new Declaration("width"))
						.addChild(new PixelLiteral("520px")))
		);
		/*
		.menu {
			color: #000000;
		}
		*/
		stylesheet.addChild((new Stylerule())
				.addChild(new ClassSelector(".menu"))
				.addChild((new Declaration("color"))
						.addChild(new ColorLiteral("#000000")))
		);

        return new AST(stylesheet);
    }

	public static AST uncheckedLevel1() {
		Stylesheet stylesheet = new Stylesheet();
		/*
			LinkColor := #ff0000;
			ParWidth := 500px;
			AdjustColor := TRUE;
			UseLinkColor := FALSE;
		 */
		stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("LinkColor"))
                .addChild(new ColorLiteral("#ff0000"))
        );
		stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("ParWidth"))
                .addChild(new PixelLiteral("500px"))
        );
		stylesheet.addChild((new VariableAssignment())
				.addChild(new VariableReference("AdjustColor"))
				.addChild(new BoolLiteral(true))
		);
		stylesheet.addChild((new VariableAssignment())
				.addChild(new VariableReference("UseLinkColor"))
				.addChild(new BoolLiteral(false))
		);
   	    /*
	        p {
	        background-color: #ffffff;
	        width: ParWidth;
            }
	    */
        stylesheet.addChild((new Stylerule())
            .addChild(new TagSelector("p"))
            .addChild((new Declaration("background-color"))
                    .addChild(new ColorLiteral("#ffffff")))
            .addChild((new Declaration("width"))
                    .addChild(new VariableReference("ParWidth")))
        );
        /*
        a {
	        color: LinkColor;
        }
        */
        stylesheet.addChild((new Stylerule())
			.addChild(new TagSelector("a"))
			.addChild((new Declaration("color"))
				.addChild(new VariableReference("LinkColor")))
		);
        /*
            #menu {
	            width: 520px;
            }
        */
        stylesheet.addChild((new Stylerule())
			.addChild(new IdSelector("#menu"))
			.addChild((new Declaration("width"))
				.addChild(new PixelLiteral("520px")))
		);
        /*
            .menu {
	            color: #000000;
            }
        */
        stylesheet.addChild((new Stylerule())
			.addChild(new ClassSelector(".menu"))
			.addChild((new Declaration("color"))
				.addChild(new ColorLiteral("#000000")))
		);
		return new AST(stylesheet);
	}

	public static AST uncheckedLevel2() {
		Stylesheet stylesheet = new Stylesheet();
		/*
			LinkColor := #ff0000;
			ParWidth := 500px;
			AdjustColor := TRUE;
			UseLinkColor := FALSE;
		 */
		stylesheet.addChild((new VariableAssignment())
				.addChild(new VariableReference("LinkColor"))
				.addChild(new ColorLiteral("#ff0000"))
		);
		stylesheet.addChild((new VariableAssignment())
				.addChild(new VariableReference("ParWidth"))
				.addChild(new PixelLiteral("500px"))
		);
		stylesheet.addChild((new VariableAssignment())
				.addChild(new VariableReference("AdjustColor"))
				.addChild(new BoolLiteral(true))
		);
		stylesheet.addChild((new VariableAssignment())
				.addChild(new VariableReference("UseLinkColor"))
				.addChild(new BoolLiteral(false))
		);
   	    /*
	        p {
	        background-color: #ffffff;
	        width: ParWidth;
            }
	    */
        stylesheet.addChild((new Stylerule())
            .addChild(new TagSelector("p"))
            .addChild((new Declaration("background-color"))
                    .addChild(new ColorLiteral("#ffffff")))
            .addChild((new Declaration("width"))
                    .addChild(new VariableReference("ParWidth")))
        );
        /*
        a {
	        color: LinkColor;
        }
        */
        stylesheet.addChild((new Stylerule())
			.addChild(new TagSelector("a"))
			.addChild((new Declaration("color"))
				.addChild(new VariableReference("LinkColor")))
		);
        /*
            #menu {
        	width: ParWidth + 2 * 10px;
            }
        */
        stylesheet.addChild((new Stylerule())
			.addChild(new IdSelector("#menu"))
			.addChild((new Declaration("width"))
				.addChild((new AddOperation())
                        .addChild(new VariableReference("ParWidth"))
                        .addChild((new MultiplyOperation())
                                .addChild(new ScalarLiteral("2") )
                                .addChild(new PixelLiteral("10px"))

        ))));
        /*
            .menu {
	            color: #000000;
            }
        */
        stylesheet.addChild((new Stylerule())
			.addChild(new ClassSelector(".menu"))
			.addChild((new Declaration("color"))
				.addChild(new ColorLiteral("#000000")))
		);
		return new AST(stylesheet);
	}

	public static AST uncheckedLevel3() {
		Stylesheet stylesheet = new Stylesheet();
		/*
			LinkColor := #ff0000;
			ParWidth := 500px;
			AdjustColor := TRUE;
			UseLinkColor := FALSE;
		 */
		stylesheet.addChild((new VariableAssignment())
				.addChild(new VariableReference("LinkColor"))
				.addChild(new ColorLiteral("#ff0000"))
		);
		stylesheet.addChild((new VariableAssignment())
				.addChild(new VariableReference("ParWidth"))
				.addChild(new PixelLiteral("500px"))
		);
		stylesheet.addChild((new VariableAssignment())
				.addChild(new VariableReference("AdjustColor"))
				.addChild(new BoolLiteral(true))
		);
		stylesheet.addChild((new VariableAssignment())
				.addChild(new VariableReference("UseLinkColor"))
				.addChild(new BoolLiteral(false))
		);
   	    /*
	        p {
				background-color: #ffffff;
				width: ParWidth;
				if[AdjustColor] {
	    			color: #124532;
	    			if[UseLinkColor]{
	        			bg-color: LinkColor;
	    			}
				}
			}
			p {
				background-color: #ffffff;
				width: ParWidth;
				if[AdjustColor] {
	    			color: #124532;
	    		if[UseLinkColor]{
	        		background-color: LinkColor;
	    		} else {
	        		background-color: #000000;
	    		}
	    		height: 20px;
			}
}
	    */
        stylesheet.addChild((new Stylerule())
				.addChild(new TagSelector("p"))
					.addChild((new Declaration("background-color"))
							.addChild(new ColorLiteral("#ffffff")))
					.addChild((new Declaration("width"))
							.addChild(new VariableReference("ParWidth")))
					.addChild((new IfClause())
						.addChild(new VariableReference("AdjustColor"))
						.addChild((new Declaration("color")
								.addChild(new ColorLiteral("#124532"))))
							.addChild((new IfClause())
									.addChild(new VariableReference("UseLinkColor"))
									.addChild(new Declaration("background-color").addChild(new VariableReference("LinkColor")))
									.addChild((new ElseClause())
											.addChild(new Declaration("background-color").addChild(new ColorLiteral("#000000")))

									)
					))
					.addChild((new Declaration("height"))
							.addChild(new PixelLiteral("20px")))
        );
        /*
        a {
	        color: LinkColor;
        }
        */
        stylesheet.addChild((new Stylerule())
			.addChild(new TagSelector("a"))
			.addChild((new Declaration("color"))
				.addChild(new VariableReference("LinkColor"))
            )
		);
        /*
            #menu {
        	width: ParWidth + 20px;
            }
        */
        stylesheet.addChild((new Stylerule())
			.addChild(new IdSelector("#menu"))
			.addChild((new Declaration("width"))
				.addChild((new AddOperation())
                        .addChild(new VariableReference("ParWidth"))
                        .addChild(new PixelLiteral("20px"))
                )
            )
        );
        /*


         .menu {
				color: #000000;
    			background-color: LinkColor;

			}

        */
        stylesheet.addChild((new Stylerule())
			.addChild(new ClassSelector(".menu"))

			.addChild((new Declaration("color"))
				.addChild(new ColorLiteral("#000000"))
            )
				.addChild((new Declaration("background-color"))
						.addChild(new VariableReference("LinkColor"))
				)

		);

		return new AST(stylesheet);
	}

	public static AST uncheckedLevel4() {
		Stylesheet stylesheet = new Stylesheet();

		/*
		 * LinkColor := #ff0000;
		 * ParWidth := 500px;
		 * AdjustColor := TRUE;
		 * UseLinkColor := FALSE;
		 */
		stylesheet.addChild(
			new VariableAssignment().addChild(new VariableReference("LinkColor")).addChild(new ColorLiteral("#ff0000"))
		);
		stylesheet.addChild(
			new VariableAssignment().addChild(new VariableReference("ParWidth")).addChild(new PixelLiteral("500px"))
		);
		stylesheet.addChild(
			new VariableAssignment().addChild(new VariableReference("AdjustColor")).addChild(new BoolLiteral(true))
		);
		stylesheet.addChild(
			new VariableAssignment().addChild(new VariableReference("UseLinkColor")).addChild(new BoolLiteral(false))
		);
		/*
		 * p {
		 *     background-color: #ffffff;
		 *     width: ParWidth;
		 *     if[AdjustColor] {
		 *         color: #124532;
		 *         if[UseLinkColor]{
		 *             background-color: LinkColor;
		 *         } else {
		 *             background-color: #000000;
		 *         }
		 *         height: 20px;
		 *     }
		 * }
		 */
		stylesheet.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("background-color").addChild(new ColorLiteral("#ffffff")))
			               .addChild(new Declaration("width").addChild(new VariableReference("ParWidth")))
			               .addChild(new IfClause().addChild(new VariableReference("AdjustColor"))
				                         .addChild(new Declaration("color").addChild(new ColorLiteral("#124532")))
				                         .addChild(new IfClause().addChild(new VariableReference("UseLinkColor"))
					                                   .addChild(new Declaration("background-color")
						                                             .addChild(new VariableReference("LinkColor")))
					                                   .addChild(new ElseClause()
						                                             .addChild(new Declaration("background-color")
							                                                       .addChild(new ColorLiteral("#000000")))

					                                   )
				                         )
			               )
			               .addChild(new Declaration("height").addChild(new PixelLiteral("20px")))
		);
		/*
		 * a {
		 *     color: LinkColor;
		 * }
		 */
		stylesheet.addChild(new Stylerule().addChild(new TagSelector("a"))
		                                   .addChild(new Declaration("color")
			                                             .addChild(new VariableReference("LinkColor")))
		);
		/*
		 * #menu {
		 *     width: ParWidth + 20px;
		 * }
		 */
		stylesheet.addChild(new Stylerule().addChild(new IdSelector("#menu"))
		                                   .addChild(new Declaration("width")
			                                             .addChild(new AddOperation()
				                                                       .addChild(new VariableReference("ParWidth"))
				                                                       .addChild(new PixelLiteral("20px"))))
		);
		/*
		 * .menu {
		 *     color: #000000;
		 *     background-color: LinkColor;
		 * }
		 */
		stylesheet.addChild(new Stylerule().addChild(new ClassSelector(".menu"))
		                                   .addChild(new Declaration("color").addChild(new ColorLiteral("#000000")))
		                                   .addChild(new Declaration("background-color")
			                                             .addChild(new VariableReference("LinkColor")))

		);
		/*
		 * div, section {
		 *     width: ParWidth;
		 *     height: ParWidth;
		 * }
		 */
		stylesheet.addChild(new Stylerule()
			                    .addChild(new TagSelector("div"))
			                    .addChild(new TagSelector("section"))
			                    .addChild(new Declaration("width").addChild(new VariableReference("ParWidth")))
			                    .addChild(new Declaration("height").addChild(new VariableReference("ParWidth")))
		);

		return new AST(stylesheet);
	}

	public static AST uncheckedLevel5() {
		Stylesheet stylesheet = new Stylesheet();

		/*
		 * LinkColor := #ff0000;
		 * ParWidth := 500px;
		 * AdjustColor := TRUE;
		 * UseLinkColor := FALSE;
		 */
		stylesheet.addChild(
			new VariableAssignment().addChild(new VariableReference("LinkColor")).addChild(new ColorLiteral("#ff0000"))
		);
		stylesheet.addChild(
			new VariableAssignment().addChild(new VariableReference("ParWidth")).addChild(new PixelLiteral("500px"))
		);
		stylesheet.addChild(
			new VariableAssignment().addChild(new VariableReference("AdjustColor")).addChild(new BoolLiteral(true))
		);
		stylesheet.addChild(
			new VariableAssignment().addChild(new VariableReference("UseLinkColor")).addChild(new BoolLiteral(false))
		);
		/*
		 * p {
		 *     background-color: #ffffff;
		 *     width: ParWidth;
		 *     if[AdjustColor] {
		 *         color: #124532;
		 *         if[UseLinkColor]{
		 *             background-color: LinkColor;
		 *         } else {
		 *             background-color: #000000;
		 *         }
		 *         height: 20px;
		 *     }
		 * }
		 */
		stylesheet.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("background-color").addChild(new ColorLiteral("#ffffff")))
			               .addChild(new Declaration("width").addChild(new VariableReference("ParWidth")))
			               .addChild(new IfClause().addChild(new VariableReference("AdjustColor"))
			                                       .addChild(new Declaration("color").addChild(new ColorLiteral(
				                                       "#124532")))
			                                       .addChild(new IfClause().addChild(new VariableReference(
				                                                               "UseLinkColor"))
			                                                               .addChild(new Declaration("background-color")
				                                                                         .addChild(new VariableReference(
					                                                                         "LinkColor")))
			                                                               .addChild(new ElseClause()
				                                                                         .addChild(new Declaration(
					                                                                         "background-color")
					                                                                                   .addChild(new ColorLiteral(
						                                                                                   "#000000")))

			                                                               )
			                                       )
			               )
			               .addChild(new Declaration("height").addChild(new PixelLiteral("20px")))
		);
		/*
		 * a {
		 *     color: LinkColor;
		 * }
		 */
		stylesheet.addChild(new Stylerule().addChild(new TagSelector("a"))
		                                   .addChild(new Declaration("color")
			                                             .addChild(new VariableReference("LinkColor")))
		);
		/*
		 * #menu {
		 *     width: ParWidth + 20px;
		 * }
		 */
		stylesheet.addChild(new Stylerule().addChild(new IdSelector("#menu"))
		                                   .addChild(new Declaration("width")
			                                             .addChild(new AddOperation()
				                                                       .addChild(new VariableReference("ParWidth"))
				                                                       .addChild(new PixelLiteral("20px"))))
		);
		/*
		 * .menu {
		 *     color: #000000;
		 *     background-color: LinkColor;
		 * }
		 */
		stylesheet.addChild(new Stylerule().addChild(new ClassSelector(".menu"))
		                                   .addChild(new Declaration("color").addChild(new ColorLiteral("#000000")))
		                                   .addChild(new Declaration("background-color")
			                                             .addChild(new VariableReference("LinkColor")))

		);
		/*
		 * div, section {
		 *     width: ParWidth;
		 *     height: ParWidth;
		 *
		 *     p {
		 *         color: LinkColor;
		 *
		 *         a, span {
		 *             color: #00ff00;
		 *         }
		 *     }
		 *
		 *     article {
		 *         background-color: #000000;
		 *
		 *         p {
		 *             color: #ffffff;
		 *         }
		 *     }
		 * }
		 */
		stylesheet.addChild(new Stylerule()
			                    .addChild(new TagSelector("div"))
			                    .addChild(new TagSelector("section"))
			                    .addChild(new Declaration("width").addChild(new VariableReference("ParWidth")))
			                    .addChild(new Declaration("height").addChild(new VariableReference("ParWidth")))
			                    .addChild(new Stylerule().addChild(new TagSelector("p"))
			                                             .addChild(new Declaration("color")
				                                                       .addChild(new VariableReference("LinkColor")))
			                                             .addChild(new Stylerule()
				                                                       .addChild(new TagSelector("a"))
				                                                       .addChild(new TagSelector("span"))
				                                                       .addChild(new Declaration("color")
					                                                                 .addChild(new ColorLiteral("#00ff00")))
			                                             )
			                    )
			                    .addChild(new Stylerule().addChild(new TagSelector("article"))
			                                             .addChild(new Declaration("background-color")
				                                                       .addChild(new ColorLiteral("#000000")))
			                                             .addChild(new Stylerule().addChild(new TagSelector("p"))
			                                                                      .addChild(new Declaration("color")
				                                                                                .addChild(new ColorLiteral("#ffffff")))
			                                             )
			                    )
		);

		return new AST(stylesheet);
	}

	public static AST uncheckedLevel6() {
		Stylesheet stylesheet = new Stylesheet();

		/*
		 * LinkColor := #ff0000;
		 * ParWidth := 500px;
		 * AdjustColor := TRUE;
		 * UseLinkColor := FALSE;
		 */
		stylesheet.addChild(
			new VariableAssignment().addChild(new VariableReference("LinkColor"))
			                        .addChild(new ColorLiteral("#ff0000"))
		);
		stylesheet.addChild(
			new VariableAssignment().addChild(new VariableReference("ParWidth"))
			                        .addChild(new PixelLiteral("500px"))
		);
		stylesheet.addChild(
			new VariableAssignment().addChild(new VariableReference("AdjustColor"))
			                        .addChild(new BoolLiteral(true))
		);
		stylesheet.addChild(
			new VariableAssignment().addChild(new VariableReference("UseLinkColor"))
			                        .addChild(new BoolLiteral(false))
		);
		/*
		 * Line() {
		 *     width: 100%;
		 *     height: 1px;
		 *     background-color: #000000;
		 * }

		 * Button(Width, Height, Color) {
		 *     width: Width;
		 *     height: Height;
		 *     color: Color;
		 * }

		 * FilledButton(Width, Height, FgColor: #000000, BgColor: FgColor) {
		 *                 Button(Width, Height, FgColor);
		 *                 background-color: BgColor;
		 * }

		 * SpecialParagraph(LinkColor: LinkColor) {
		 *     p {
		 *         width: ParWidth;
		 *         a {
		 *             color: LinkColor;
		 *         }
		 *     }
		 * }
		 */
		stylesheet.addChild(new Mixin("Line")
		                               .addChild(new Declaration("width").addChild(new PercentageLiteral("100%")))
		                               .addChild(new Declaration("height").addChild(new PixelLiteral("1px")))
		                               .addChild(new Declaration("background-color").addChild(new ColorLiteral("#000000")))
		);
		stylesheet.addChild(new Mixin("Button")
		                               .addChild(new VariableAssignment().addChild(new VariableReference("Width")))
		                               .addChild(new VariableAssignment().addChild(new VariableReference("Height")))
		                               .addChild(new VariableAssignment().addChild(new VariableReference("Color")))
		                               .addChild(new Declaration("width").addChild(new VariableReference("Width")))
		                               .addChild(new Declaration("height").addChild(new VariableReference("Height")))
		                               .addChild(new Declaration("color").addChild(new VariableReference("Color")))
		);
		stylesheet.addChild(new Mixin("FilledButton")
		                               .addChild(new VariableAssignment().addChild(new VariableReference("Width")))
		                               .addChild(new VariableAssignment().addChild(new VariableReference("Height")))
		                               .addChild(new VariableAssignment()
			                                         .addChild(new VariableReference("FgColor"))
			                                         .addChild(new ColorLiteral("#000000")))
		                               .addChild(new VariableAssignment()
			                                         .addChild(new VariableReference("BgColor"))
			                                         .addChild(new VariableReference("FgColor")))
		                               .addChild(new MixinCall("Button")
		                                                        .addChild(new VariableReference("Width"))
		                                                        .addChild(new VariableReference("Height"))
		                                                        .addChild(new VariableReference("FgColor")))
		                               .addChild(new Declaration("background-color")
			                                         .addChild(new VariableReference("BgColor")))
		);
		stylesheet.addChild(new Mixin("SpecialParagraph")
		                               .addChild(new VariableAssignment()
			                                         .addChild(new VariableReference("LinkColor"))
			                                         .addChild(new VariableReference("LinkColor")))
		                               .addChild(new Stylerule().addChild(new TagSelector("p"))
		                                                        .addChild(new Declaration("width")
			                                                                  .addChild(new VariableReference("ParWidth")))
		                                                        .addChild(new Stylerule().addChild(new TagSelector("a"))
		                                                                                 .addChild(new Declaration("color")
			                                                                                           .addChild(new VariableReference("LinkColor")))
		                                                        )
		                               )
		);
		/*
		 * p {
		 *     background-color: #ffffff;
		 *     width: ParWidth;
		 *     if[AdjustColor] {
		 *         color: #124532;
		 *         if[UseLinkColor]{
		 *             background-color: LinkColor;
		 *         } else {
		 *             background-color: #000000;
		 *         }
		 *         height: 20px;
		 *     }
		 * }
		 */
		stylesheet.addChild(
			new Stylerule().addChild(new TagSelector("p"))
			               .addChild(new Declaration("background-color").addChild(new ColorLiteral("#ffffff")))
			               .addChild(new Declaration("width").addChild(new VariableReference("ParWidth")))
			               .addChild(new IfClause().addChild(new VariableReference("AdjustColor"))
			                                       .addChild(new Declaration("color").addChild(new ColorLiteral(
				                                       "#124532")))
			                                       .addChild(new IfClause().addChild(new VariableReference(
				                                                               "UseLinkColor"))
			                                                               .addChild(new Declaration("background-color")
				                                                                         .addChild(new VariableReference(
					                                                                         "LinkColor")))
			                                                               .addChild(new ElseClause()
				                                                                         .addChild(new Declaration(
					                                                                         "background-color")
					                                                                                   .addChild(new ColorLiteral(
						                                                                                   "#000000")))

			                                                               )
			                                       )
			               )
			               .addChild(new Declaration("height").addChild(new PixelLiteral("20px")))
		);
		/*
		 * a {
		 *     color: LinkColor;
		 * }
		 */
		stylesheet.addChild(new Stylerule().addChild(new TagSelector("a"))
		                                   .addChild(new Declaration("color")
			                                             .addChild(new VariableReference("LinkColor")))
		);
		/*
		 * #menu {
		 *     width: ParWidth + 20px;
		 * }
		 */
		stylesheet.addChild(new Stylerule().addChild(new IdSelector("#menu"))
		                                   .addChild(new Declaration("width")
			                                             .addChild(new AddOperation()
				                                                       .addChild(new VariableReference("ParWidth"))
				                                                       .addChild(new PixelLiteral("20px"))))
		);
		/*
		 * .menu {
		 *     color: #000000;
		 *     background-color: LinkColor;
		 * }
		 */
		stylesheet.addChild(new Stylerule().addChild(new ClassSelector(".menu"))
		                                   .addChild(new Declaration("color").addChild(new ColorLiteral("#000000")))
		                                   .addChild(new Declaration("background-color")
			                                             .addChild(new VariableReference("LinkColor")))

		);
		/*
		 * div, section {
		 *     width: ParWidth;
		 *     height: ParWidth;
		 *
		 *     p {
		 *         color: LinkColor;
		 *
		 *         a, span {
		 *             color: #00ff00;
		 *         }
		 *     }
		 *
		 *     article {
		 *         background-color: #000000;
		 *
		 *         p {
		 *             color: #ffffff;
		 *         }
		 *     }
		 * }
		 */
		stylesheet.addChild(new Stylerule()
			                    .addChild(new TagSelector("div"))
			                    .addChild(new TagSelector("section"))
			                    .addChild(new Declaration("width").addChild(new VariableReference("ParWidth")))
			                    .addChild(new Declaration("height").addChild(new VariableReference("ParWidth")))
			                    .addChild(new Stylerule().addChild(new TagSelector("p"))
			                                             .addChild(new Declaration("color")
				                                                       .addChild(new VariableReference("LinkColor")))
			                                             .addChild(new Stylerule()
				                                                       .addChild(new TagSelector("a"))
				                                                       .addChild(new TagSelector("span"))
				                                                       .addChild(new Declaration("color")
					                                                                 .addChild(new ColorLiteral(
						                                                                 "#00ff00")))
			                                             )
			                    )
			                    .addChild(new Stylerule().addChild(new TagSelector("article"))
			                                             .addChild(new Declaration("background-color")
				                                                       .addChild(new ColorLiteral("#000000")))
			                                             .addChild(new Stylerule().addChild(new TagSelector("p"))
			                                                                      .addChild(new Declaration("color")
				                                                                                .addChild(new ColorLiteral(
					                                                                                "#ffffff")))
			                                             )
			                    )
		);
		/*
		 * #line {
		 *     Line();
		 * }
		 */
		stylesheet.addChild(new Stylerule().addChild(new IdSelector("#line")).addChild(new MixinCall("Line")));
		/*
		 * #button-a, #button-b {
		 *     Button(80px, 40px, #00ff00);
		 * }
		 */
		stylesheet.addChild(new Stylerule()
			                    .addChild(new IdSelector("#button-a"))
			                    .addChild(new IdSelector("#button-b"))
			                    .addChild(new MixinCall("Button")
				                              .addChild(new PixelLiteral("80px"))
				                              .addChild(new PixelLiteral("40px"))
				                              .addChild(new ColorLiteral("#00ff00"))
			                    )
		);
		/*
		 * #filled-button {
		 *     FilledButton(100px, 50px, #444444);
		 * }
		 */
		stylesheet.addChild(new Stylerule().addChild(new IdSelector("#filled-button"))
		                                   .addChild(new MixinCall("FilledButton")
		                                                            .addChild(new PixelLiteral("100px"))
		                                                            .addChild(new PixelLiteral("50px"))
		                                                            .addChild(new ColorLiteral("#444444"))
		                                   )
		);
		/*
		 * #highlighted-section {
		 *     background-color: #000000;
		 *     SpecialParagraph(#123456);
		 * }
		 */
		stylesheet.addChild(new Stylerule().addChild(new IdSelector("#highlighted-section"))
		                                   .addChild(new Declaration("background-color").addChild(new ColorLiteral("#000000")))
		                                   .addChild(new MixinCall("SpecialParagraph").addChild(new ColorLiteral("#123546")))
		);

		return new AST(stylesheet);
	}

}
