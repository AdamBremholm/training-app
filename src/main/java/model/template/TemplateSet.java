package model.template;

public class TemplateSet {

    private  String setId;
    private  int repetitions;
    private  double weight;

    @SuppressWarnings("unused")
    public TemplateSet() {
    }

    @SuppressWarnings("unused")
    public String getSetId() {
        return setId;
    }

    @SuppressWarnings("unused")
    public void setSetId(String setId) {
        this.setId = setId;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
