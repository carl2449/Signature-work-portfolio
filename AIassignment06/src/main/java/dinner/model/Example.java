package dinner.model;

public class Example {
    private DayOfWeek day;
    private Boolean haveLeftovers;
    private Boolean sickOfLeftovers;
    private Boolean reallyHungry;
    private Boolean havePizza;
    private Boolean alreadyAtePizzaThisWeek;
    private Boolean tired;
    private Decision decision;

    public Example(DayOfWeek day, Boolean haveLeftovers, Boolean sickOfLeftovers, Boolean reallyHungry, Boolean havePizza, Boolean alreadyAtePizzaThisWeek, Boolean tired, Decision decision) {
        this.day = day;
        this.haveLeftovers = haveLeftovers;
        this.sickOfLeftovers = sickOfLeftovers;
        this.reallyHungry = reallyHungry;
        this.havePizza = havePizza;
        this.alreadyAtePizzaThisWeek = alreadyAtePizzaThisWeek;
        this.tired = tired;
        this.decision = decision;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public boolean isHaveLeftovers() {
        return haveLeftovers;
    }

    public void setHaveLeftovers(Boolean haveLeftovers) {
        this.haveLeftovers = haveLeftovers;
    }

    public boolean isSickOfLeftovers() {
        return sickOfLeftovers;
    }

    public void setSickOfLeftovers(Boolean sickOfLeftovers) {
        this.sickOfLeftovers = sickOfLeftovers;
    }

    public boolean isReallyHungry() {
        return reallyHungry;
    }

    public void setReallyHungry(Boolean reallyHungry) {
        this.reallyHungry = reallyHungry;
    }

    public boolean isHavePizza() {
        return havePizza;
    }

    public void setHavePizza(Boolean havePizza) {
        this.havePizza = havePizza;
    }

    public boolean isAlreadyAtePizzaThisWeek() {
        return alreadyAtePizzaThisWeek;
    }

    public void setAlreadyAtePizzaThisWeek(Boolean alreadyAtePizzaThisWeek) {
        this.alreadyAtePizzaThisWeek = alreadyAtePizzaThisWeek;
    }

    public boolean isTired() {
        return tired;
    }

    public void setTired(Boolean tired) {
        this.tired = tired;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public Object getAttributeValue(Attribute attribute) {
        if(Attribute.DAY.equals(attribute)) {
            return this.day;
        } else if(Attribute.ALREADY_ATE_PIZZA.equals(attribute)) {
            return this.alreadyAtePizzaThisWeek;
        } else if(Attribute.HAVE_LEFTOVERS.equals(attribute)) {
            return this.haveLeftovers;
        } else if(Attribute.HAVE_PIZZA.equals(attribute)) {
            return this.havePizza;
        } else if(Attribute.REALLY_HUNGRY.equals(attribute)) {
            return this.reallyHungry;
        } else if(Attribute.SICK_OF_LEFTOVERS.equals(attribute)) {
            return this.sickOfLeftovers;
        } else if(Attribute.TIRED.equals(attribute)) {
            return this.tired;
        } else {
            return null;
        }
    }
}
