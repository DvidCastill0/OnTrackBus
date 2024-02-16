﻿using Newtonsoft.Json;

namespace RoutesGoogleMapsScraper.Models
{
    public class OnTrackBusActivityReport
    {
        [JsonProperty("Id_ReporteMA")]
        public int Id { get; set; }
        [JsonProperty("FechaAbordado")]
        public string BoardingDateOnly { get; set; }
        [JsonProperty("HoraAbordado")]
        public string BoardingTimeOnly { get; set; }
        public string BoardingDateTime { get; set; }
        [JsonProperty("ParadaAbordada")]
        public string StandName { get; set; }
        [JsonProperty("RutaAbordada")]
        public string RouteName { get; set; }

        public OnTrackBusActivityReport()
        {
            Id = 0;
            BoardingDateOnly = DateTime.Now.ToString("HH:mm:ss d-MM-yyyy");
            BoardingTimeOnly = DateTime.Now.ToString("HH:mm:ss d-MM-yyyy");
            BoardingDateTime = DateTime.Now.ToString("HH:mm:ss d-MM-yyyy");
            StandName = string.Empty;
            RouteName = string.Empty;
        }
    }
}