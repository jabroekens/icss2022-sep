package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;

public class Generator {

	public String generate(AST ast) {
		var sb = new StringBuilder();
		traverse(ast.root, sb);
		return sb.toString();
	}

	public void traverse(ASTNode node, StringBuilder sb) {
		if (node instanceof Stylerule stylerule) {
			stylerule.selectors.forEach(sl -> {
				sb.append(sl.toString());
				sb.append(", ");
			});

			sb.delete(sb.length() - ", ".length(), sb.length());
			sb.append(" {").append(System.lineSeparator());

			stylerule.body.forEach(b -> {
				if (b instanceof Declaration d) {
					sb.append("  ").append(d.property.name).append(": ");

					if (d.expression instanceof ScalarLiteral sl) {
						sb.append(sl.value);
					} else if (d.expression instanceof BoolLiteral bl) {
						sb.append(bl.value);
					} else if (d.expression instanceof ColorLiteral cl) {
						sb.append(cl.value);
					} else if (d.expression instanceof PercentageLiteral pl) {
						sb.append(pl.value).append("%");
					} else if (d.expression instanceof PixelLiteral pl) {
						sb.append(pl.value).append("px");
					}

					sb.append(";").append(System.lineSeparator());
				}
			});

			sb.append("}").append(System.lineSeparator());
		} else {
			node.getChildren().forEach(i -> traverse(i, sb));
		}
	}

}
