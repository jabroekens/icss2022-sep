package nl.han.ica.icss.ast.selectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Selector;

public class NestedSelector extends Selector {

	public List<Selector> selectors = new ArrayList<>();

	public NestedSelector(Selector... selectors) {
		Collections.addAll(this.selectors, selectors);
	}

	@Override
	public String getNodeLabel() {
		return "NestedSelector " + this;
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		selectors.forEach(s -> sb.append(s).append(" "));
		sb.delete(sb.length() - " ".length(), sb.length());
		return sb.toString();
	}

	@Override
	public ASTNode addChild(ASTNode child) {
		if (child instanceof Selector s) {
			selectors.add(s);
		}
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		NestedSelector that = (NestedSelector) o;
		return Objects.equals(selectors, that.selectors);
	}

	@Override
	public int hashCode() {
		return Objects.hash(selectors);
	}

}
