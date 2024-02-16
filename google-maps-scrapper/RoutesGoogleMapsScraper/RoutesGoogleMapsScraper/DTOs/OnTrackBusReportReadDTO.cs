namespace RoutesGoogleMapsScraper.DTOs
{
    public class OnTrackBusReportReadDTO
    {
        public string Id { get; set; }
        public DateTime IssuedDateTime { get; set; }
        public DateTime ChannelReportDateTime { get; set; }
        public string Email { get; set; }
        public string ChannelReportId { get; set; }
        public string Location { get; set; }
        public string UnitNumber { get; set; }
    }
}
