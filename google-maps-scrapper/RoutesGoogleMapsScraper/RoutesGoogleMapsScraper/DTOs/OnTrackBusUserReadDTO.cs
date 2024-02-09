using Newtonsoft.Json;
using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.DTOs
{
    public class OnTrackBusUserReadDTO
    {
        [JsonProperty("iduser")]
        public string Id { get; set; }
        [JsonProperty("nombre")]
        public string Firstname { get; set; }
        [JsonProperty("apellido")]
        public string Lastname { get; set; }
        [JsonProperty("correo")]
        public string Email { get; set; }
        [JsonProperty("contactoConfianza")]
        public string TrustContact { get; set; }
        [JsonProperty("RutaMasFrencuentada")]
        public List<string> TopRoutes { get; set; }
        [JsonProperty("MiActividad")]
        public Dictionary<string, OnTrackBusActivityReport> ActivityLog { get; set; }
        [JsonProperty("CanalesSeleccionados")]
        public Dictionary<string, OnTrackBusSelectedChannel> SelectedChannels { get; set; }

        public OnTrackBusUserReadDTO()
        {
            Id = string.Empty;
            Firstname = string.Empty;
            Lastname = string.Empty;
            Email = string.Empty;
            TrustContact = string.Empty;
            TopRoutes = new List<string>();
            ActivityLog = new Dictionary<string, OnTrackBusActivityReport>();
            SelectedChannels = new Dictionary<string, OnTrackBusSelectedChannel>();
        }
    }
}
