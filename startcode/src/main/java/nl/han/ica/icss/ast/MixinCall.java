package nl.han.ica.icss.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MixinCall extends ASTNode {

	public VariableReference name;
	public List<Expression> arguments = new ArrayList<>();

	public MixinCall() {
	}

	public MixinCall(String name) {
		this.name = new VariableReference(name);
	}

	@Override
	public String getNodeLabel() {
		return "MixinCall";
	}

	@Override
	public ArrayList<ASTNode> getChildren() {
		var children = new ArrayList<ASTNode>();
		children.add(name);
		children.addAll(arguments);
		return children;
	}

	@Override
	public ASTNode addChild(ASTNode child) {
		if (name == null && child instanceof VariableReference v) {
			name = v;
		} else if (child instanceof Expression e) {
			arguments.add(e);
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
		MixinCall mixinCall = (MixinCall) o;
		return Objects.equals(name, mixinCall.name) && Objects.equals(arguments, mixinCall.arguments);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, arguments);
	}

}
