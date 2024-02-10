using Newtonsoft.Json;

namespace RoutesGoogleMapsScraper.Models
{
    public class OnTrackBusReport
    {
        [JsonProperty("Id_Clave")]
        public string Id { get; set; }
        [JsonProperty("FechaDeEmision")]
        public string IssuedDateTime { get; set; }
        [JsonProperty("Hora_DeReporteCanal")]
        public string ChannelReportDateTime { get; set; }
        [JsonProperty("correoDelUsuario")]
        public string Email { get; set; }
        [JsonProperty("Id_ReporteCanal")]
        public string ChannelReportId { get; set; }
        [JsonProperty("LugarDelReporte")]
        public string Location { get; set; }
        [JsonProperty("numeroDeUnidad")]
        public string UnitNumber { get; set; }

        public OnTrackBusReport()
        {
            Id = string.Empty;
            IssuedDateTime = DateTime.Now.ToString("HH:mm:ss d-MM-yyyy");
            ChannelReportDateTime = DateTime.Now.ToString("HH:mm:ss d-MM-yyyy");
            Email = string.Empty;
            ChannelReportId = string.Empty;
            Location = string.Empty;
            UnitNumber = string.Empty;
        }
    }
}
