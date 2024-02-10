namespace RoutesGoogleMapsScraper.DTOs
{
    public class OnTrackBusUserReadDTO
    {
        public string Id { get; set; }
        public string Firstname { get; set; }
        public string Lastname { get; set; }
        public string Email { get; set; }
        public string TrustContact { get; set; }
        public List<string> TopRoutes { get; set; }
        public Dictionary<string, Dictionary<string, OnTrackBusActivityReportReadDTO>> ActivityLog { get; set; }
        public Dictionary<string, OnTrackBusSelectedChannelReadDTO> SelectedChannels { get; set; }
    }
}
