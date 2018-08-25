package com.it.soul.lab.sql.query.models;

public class PredicateExpression implements Predicate {
	
	public PredicateExpression(String key, Object value, Operator type) {
		ExpressionInterpreter exp = new Expression(new Property(key, value, DataType.OBJECT), type);
		_proxy = new PredicateProxy(exp);
	}
	
	public PredicateExpression(Property property, Operator type) {
		ExpressionInterpreter exp = new Expression(property, type);
		_proxy = new PredicateProxy(exp);
	}
	
	private PredicateProxy _proxy;
	private PredicateProxy getProxy() {
		return _proxy;
	}

	@Override
	public String interpret() {
		return _proxy.interpret();
	}

	@Override
	public Expression[] resolveExpressions() {
		return _proxy.resolveExpressions();
	}

	@Override
	public Predicate and(ExpressionInterpreter exp) {
		PredicateProxy proxy = getProxy();
		proxy.createAnd(exp);
		return this;
	}

	@Override
	public Predicate or(ExpressionInterpreter exp) {
		PredicateProxy proxy = getProxy();
		proxy.createOr(exp);
		return this;
	}
	
	@Override
	public Predicate not() {
		PredicateProxy proxy = getProxy();
		proxy.createNor();
		return this;
	}

	private class PredicateProxy implements ExpressionInterpreter{
		
		private ExpressionInterpreter expression;

		public PredicateProxy(ExpressionInterpreter expression) {
			this.expression = expression;
		}

		@Override
		public String interpret() {
			return expression.interpret();
		}

		@Override
		public Expression[] resolveExpressions() {
			return expression.resolveExpressions();
		}
		
		public void createAnd(ExpressionInterpreter exp) {
			expression = new AndExpression(expression, exp);
		}
		
		public void createOr(ExpressionInterpreter exp) {
			expression = new OrExpression(expression, exp);
		}
		
		public void createNor() {
			expression = new NotExpression(expression);
		}
		
	}

	@Override
	public Predicate and(String key, Object value, Operator opt) {
		Predicate pred = and(new Expression(new Property(key, value, DataType.OBJECT), opt));
		return pred;
	}

	@Override
	public Predicate or(String key, Object value, Operator opt) {
		Predicate pred = or(new Expression(new Property(key, value, DataType.OBJECT), opt));
		return pred;
	}

}
