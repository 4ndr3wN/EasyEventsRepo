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
    String eventLongitude;
    String eventLatitude;
    String idEvent;

    Boolean ticketAvailable;

    double ratingEvent;
    double ratingUsers;
    int ratingVenue;
    int ratingWeather;

    long dateEvent;


    public Event(String address, String country, String city, String info, String picture, String ticket, String title, String venue,
                 String longitude, String latitude, double ratingEvent, double ratingUsers, int ratingVenue,
                 int ratingWeather, long date, String id, Boolean ticketAvailable) {

        this.addressEvent = address; //get from api
        this.countryEvent = country;
        this.cityEvent = city;
        this.infoEvent = info;
        this.pictureEvent = picture;
        this.ticketEvent = ticket;
        this.titleEvent = title;
        this.venueEvent = venue;

        this.eventLatitude = latitude;
        this.eventLongitude = longitude;
        this.ratingEvent = ratingEvent;
        this.ratingUsers = ratingUsers;
        this.ratingWeather = ratingWeather;
        this.ratingVenue = ratingVenue;

        this.dateEvent = date;
        this.idEvent = id;

        this.ticketAvailable = ticketAvailable;
    }

    public void updateRatingVenue(int rating){
        this.ratingVenue = rating;
    }

    public void updateRatingWeather(int rating){
        this.ratingWeather = rating;
    }
}
