namespace RoutesGoogleMapsScraper.DTOs
{
    public class OnTrackBusChannelReadDTO
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public Dictionary<string, OnTrackBusReportReadDTO> Reports { get; set; }
        public Dictionary<string, OnTrackBusSuggestionReadDTO> Suggestions { get; set; }
        public float Price { get; set; }
    }
}
