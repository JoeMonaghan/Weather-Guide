package model;

/**
 * Created by joe on 01/03/2017.
 */

public class Weather {

    public Place place = new Place();
    public String iconData;
    public CurrentCondition currentCondition = new CurrentCondition();
    public Temperature temperature = new Temperature();
    public Wind wind = new Wind();
    public Snow snow = new Snow();
    public Cloud cloud = new Cloud();
}
