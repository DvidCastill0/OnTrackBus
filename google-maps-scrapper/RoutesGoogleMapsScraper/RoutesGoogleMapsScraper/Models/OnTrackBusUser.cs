using Newtonsoft.Json;

namespace RoutesGoogleMapsScraper.Models
{
    public class OnTrackBusUser
    {
        [JsonProperty("iduser")]
        public string Id { get; set; }
        [JsonProperty("nombre")]
        public string Firstname { get; set; }
        [JsonProperty("apellido")]
        public string Lastname { get; set; }
        [JsonProperty("correo")]
        public string Email { get; set; }
        [JsonProperty("contraseña")]
        public string Password { get; set; }
        [JsonProperty("contactoConfianza")]
        public string TrustContact { get; set; }
        [JsonProperty("RutaMasFrencuentada")]
        public List<string> TopRoutes { get; set; }
        [JsonProperty("MiActividad")]
        public Dictionary<string, Dictionary<string, OnTrackBusActivityReport>> ActivityLog { get; set; }
        [JsonProperty("CanalesSeleccionados")]
        public Dictionary<string, OnTrackBusSelectedChannel> SelectedChannels { get; set; }

        public OnTrackBusUser()
        {
            Id = string.Empty;
            Firstname = string.Empty;
            Lastname = string.Empty;
            Email = string.Empty;
            Password = string.Empty;
            TrustContact = string.Empty;
            TopRoutes = new List<string>();
            ActivityLog = new Dictionary<string, Dictionary<string, OnTrackBusActivityReport>>();
            SelectedChannels = new Dictionary<string, OnTrackBusSelectedChannel>();
        }
    }
}
