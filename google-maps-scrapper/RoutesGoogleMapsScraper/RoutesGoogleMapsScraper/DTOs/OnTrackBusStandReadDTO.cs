namespace RoutesGoogleMapsScraper.DTOs
{
    public class OnTrackBusStandReadDTO
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public float Latitude { get; set; }
        public float Longitude { get; set; }
        public DateTime Prediction { get; set; }
        public DateTime LastTimeBoarded { get; set; }
    }
}
