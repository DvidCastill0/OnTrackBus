namespace RoutesGoogleMapsScraper.DTOs
{
    public class OnTrackBusActivityReportReadDTO
    {
        public int Id { get; set; }
        public string UserId { get; set; }
        public DateTime BoardingDateTime { get; set; }
        public string StandName { get; set; }
        public string RouteName { get; set; }
    }
}
