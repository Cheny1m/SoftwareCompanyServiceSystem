package cn.edu.sicnu.cs.model;

import java.util.ArrayList;
import java.util.List;

public class Front_dic_itemExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public Front_dic_itemExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andFdiIdIsNull() {
            addCriterion("fdi_id is null");
            return (Criteria) this;
        }

        public Criteria andFdiIdIsNotNull() {
            addCriterion("fdi_id is not null");
            return (Criteria) this;
        }

        public Criteria andFdiIdEqualTo(Integer value) {
            addCriterion("fdi_id =", value, "fdiId");
            return (Criteria) this;
        }

        public Criteria andFdiIdNotEqualTo(Integer value) {
            addCriterion("fdi_id <>", value, "fdiId");
            return (Criteria) this;
        }

        public Criteria andFdiIdGreaterThan(Integer value) {
            addCriterion("fdi_id >", value, "fdiId");
            return (Criteria) this;
        }

        public Criteria andFdiIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("fdi_id >=", value, "fdiId");
            return (Criteria) this;
        }

        public Criteria andFdiIdLessThan(Integer value) {
            addCriterion("fdi_id <", value, "fdiId");
            return (Criteria) this;
        }

        public Criteria andFdiIdLessThanOrEqualTo(Integer value) {
            addCriterion("fdi_id <=", value, "fdiId");
            return (Criteria) this;
        }

        public Criteria andFdiIdIn(List<Integer> values) {
            addCriterion("fdi_id in", values, "fdiId");
            return (Criteria) this;
        }

        public Criteria andFdiIdNotIn(List<Integer> values) {
            addCriterion("fdi_id not in", values, "fdiId");
            return (Criteria) this;
        }

        public Criteria andFdiIdBetween(Integer value1, Integer value2) {
            addCriterion("fdi_id between", value1, value2, "fdiId");
            return (Criteria) this;
        }

        public Criteria andFdiIdNotBetween(Integer value1, Integer value2) {
            addCriterion("fdi_id not between", value1, value2, "fdiId");
            return (Criteria) this;
        }

        public Criteria andFdiTypeIdIsNull() {
            addCriterion("fdi_type_id is null");
            return (Criteria) this;
        }

        public Criteria andFdiTypeIdIsNotNull() {
            addCriterion("fdi_type_id is not null");
            return (Criteria) this;
        }

        public Criteria andFdiTypeIdEqualTo(Integer value) {
            addCriterion("fdi_type_id =", value, "fdiTypeId");
            return (Criteria) this;
        }

        public Criteria andFdiTypeIdNotEqualTo(Integer value) {
            addCriterion("fdi_type_id <>", value, "fdiTypeId");
            return (Criteria) this;
        }

        public Criteria andFdiTypeIdGreaterThan(Integer value) {
            addCriterion("fdi_type_id >", value, "fdiTypeId");
            return (Criteria) this;
        }

        public Criteria andFdiTypeIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("fdi_type_id >=", value, "fdiTypeId");
            return (Criteria) this;
        }

        public Criteria andFdiTypeIdLessThan(Integer value) {
            addCriterion("fdi_type_id <", value, "fdiTypeId");
            return (Criteria) this;
        }

        public Criteria andFdiTypeIdLessThanOrEqualTo(Integer value) {
            addCriterion("fdi_type_id <=", value, "fdiTypeId");
            return (Criteria) this;
        }

        public Criteria andFdiTypeIdIn(List<Integer> values) {
            addCriterion("fdi_type_id in", values, "fdiTypeId");
            return (Criteria) this;
        }

        public Criteria andFdiTypeIdNotIn(List<Integer> values) {
            addCriterion("fdi_type_id not in", values, "fdiTypeId");
            return (Criteria) this;
        }

        public Criteria andFdiTypeIdBetween(Integer value1, Integer value2) {
            addCriterion("fdi_type_id between", value1, value2, "fdiTypeId");
            return (Criteria) this;
        }

        public Criteria andFdiTypeIdNotBetween(Integer value1, Integer value2) {
            addCriterion("fdi_type_id not between", value1, value2, "fdiTypeId");
            return (Criteria) this;
        }

        public Criteria andFdiNameIsNull() {
            addCriterion("fdi_name is null");
            return (Criteria) this;
        }

        public Criteria andFdiNameIsNotNull() {
            addCriterion("fdi_name is not null");
            return (Criteria) this;
        }

        public Criteria andFdiNameEqualTo(String value) {
            addCriterion("fdi_name =", value, "fdiName");
            return (Criteria) this;
        }

        public Criteria andFdiNameNotEqualTo(String value) {
            addCriterion("fdi_name <>", value, "fdiName");
            return (Criteria) this;
        }

        public Criteria andFdiNameGreaterThan(String value) {
            addCriterion("fdi_name >", value, "fdiName");
            return (Criteria) this;
        }

        public Criteria andFdiNameGreaterThanOrEqualTo(String value) {
            addCriterion("fdi_name >=", value, "fdiName");
            return (Criteria) this;
        }

        public Criteria andFdiNameLessThan(String value) {
            addCriterion("fdi_name <", value, "fdiName");
            return (Criteria) this;
        }

        public Criteria andFdiNameLessThanOrEqualTo(String value) {
            addCriterion("fdi_name <=", value, "fdiName");
            return (Criteria) this;
        }

        public Criteria andFdiNameLike(String value) {
            addCriterion("fdi_name like", value, "fdiName");
            return (Criteria) this;
        }

        public Criteria andFdiNameNotLike(String value) {
            addCriterion("fdi_name not like", value, "fdiName");
            return (Criteria) this;
        }

        public Criteria andFdiNameIn(List<String> values) {
            addCriterion("fdi_name in", values, "fdiName");
            return (Criteria) this;
        }

        public Criteria andFdiNameNotIn(List<String> values) {
            addCriterion("fdi_name not in", values, "fdiName");
            return (Criteria) this;
        }

        public Criteria andFdiNameBetween(String value1, String value2) {
            addCriterion("fdi_name between", value1, value2, "fdiName");
            return (Criteria) this;
        }

        public Criteria andFdiNameNotBetween(String value1, String value2) {
            addCriterion("fdi_name not between", value1, value2, "fdiName");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}