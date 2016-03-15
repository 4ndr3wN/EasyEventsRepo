package tue.easyevents;

/**
 * Created by s137092 on 8-3-2016.
 */
public class Event {

    String addressEvent;
    String countryEvent;
    String cityEvent;
    String infoEvent;
    String pictureEvent;
    String ticketEvent;
    String titleEvent;
    String venueEvent;
    String eventLongtitude;
    String eventLatitude;
    String idEvent;


    double ratingEvent;
    double ratingUsers;
    double ratingVenue;
    double ratingWeather;

    long dateEvent;


    public Event(String address, String country, String city, String info, String picture, String ticket, String title, String venue,
                 String longtitude, String latitude, double ratingEvent, double ratingUsers, double ratingVenue,
                 double ratingWeather, long date, String id) {

        this.addressEvent = address; //get from api
        this.countryEvent = country;
        this.cityEvent = cityEvent;
        this.infoEvent = info;
        this.pictureEvent = picture;
        this.ticketEvent = ticket;
        this.titleEvent = title;
        this.venueEvent = venue;

        this.eventLatitude = longtitude;
        this.eventLongtitude = latitude;
        this.ratingEvent = ratingEvent;
        this.ratingUsers = ratingUsers;
        this.ratingWeather = ratingWeather;
        this.ratingVenue = ratingVenue;

        this.dateEvent = date;
        this.idEvent = id;
    }
}
