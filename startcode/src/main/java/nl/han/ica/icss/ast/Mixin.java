package nl.han.ica.icss.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Mixin extends ASTNode {

	public VariableReference name;
	public List<VariableAssignment> arguments = new ArrayList<>();
	public List<ASTNode> body = new ArrayList<>();

	public Mixin() {
	}

	public Mixin(String name) {
		this.name = new VariableReference(name);
	}

	@Override
	public String getNodeLabel() {
		return "Mixin";
	}

	@Override
	public ArrayList<ASTNode> getChildren() {
		var children = new ArrayList<ASTNode>();
		children.add(name);
		children.addAll(arguments);
		children.addAll(body);
		return children;
	}

	@Override
	public ASTNode addChild(ASTNode child) {
		if (name == null && child instanceof VariableReference v) {
			name = v;
		} else if (child instanceof VariableAssignment va) {
			arguments.add(va);
		} else {
			body.add(child);
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
		Mixin mixin = (Mixin) o;
		return Objects.equals(name, mixin.name) && Objects.equals(arguments, mixin.arguments);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, arguments);
	}

}
