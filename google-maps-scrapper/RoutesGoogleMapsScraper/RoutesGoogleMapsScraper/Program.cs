using AutoMapper;
using Google.Maps;
using Google.Maps.Direction;
using Newtonsoft.Json;
using RoutesGoogleMapsScraper.APIs;
using RoutesGoogleMapsScraper.Configuration;
using RoutesGoogleMapsScraper.Models;
using Serilog;
using Serilog.Sinks.SystemConsole.Themes;
using System.Diagnostics;

namespace RoutesGoogleMapsScraper
{
    public class Program
    {
        public static void Main(string[] args)
        {
            #region Serilog Configuration
            using var log = new LoggerConfiguration()
                .WriteTo.Console(theme: SystemConsoleTheme.Literate)
                .CreateLogger();
            #endregion

            #region AutoMapper Configuration
            log.Information("AutoMapper configuration process started");
            var autoMapperConfig = new AutoMapperStartupConfiguration();
            IMapper mapper = autoMapperConfig.Configuration.CreateMapper();
            log.Information("AutoMapper configuration process finished");
            #endregion

            #region OnTrackBus Resource Extraction
            log.Information("OnTrackBus resource extraction process started");
            var stopWatch = new Stopwatch();
            var hostname = Environment.GetEnvironmentVariable("ONTRACKBUS_HOSTNAME") ?? "ontrackbus.com";
            var onTrackBusAPI = new OnTrackBusAPI("https", hostname, 443);
            onTrackBusAPI.BuildUri();
            log.Information("Extracting resource information from OnTrackBus");
            stopWatch.Start();
            var onTrackBusStringReponse = onTrackBusAPI.GetResource();
            int responseSize = !string.IsNullOrWhiteSpace(onTrackBusStringReponse) ? onTrackBusStringReponse.Length : 0;
            stopWatch.Stop();
            log.Information($"OnTrackBus resource extraction done in {stopWatch.ElapsedMilliseconds} ms. Resource size: {responseSize} bytes");
            #endregion

            var onTrackBusResponse = JsonConvert.DeserializeObject<OnTrackBusResponse>(onTrackBusStringReponse);

            if (onTrackBusResponse == null)
            {
                log.Error("OnTrackBus response is null. There is not data to use for extracting google maps directions data");
                return;
            }

            GoogleSigned.AssignAllServices(new GoogleSigned(Environment.GetEnvironmentVariable("GOOGLE_MAPS_API_KEY")));

            var directionsRequest = new DirectionRequest();
            double originLatitude, originLongitude, destinationLatitude, destinationLongitude;
            originLatitude = 20.63327;
            originLongitude = -103.23679;
            destinationLatitude = 20.6285;
            destinationLongitude = -103.24276;
            directionsRequest.Origin = new LatLng(originLatitude, originLongitude);
            directionsRequest.Destination = new LatLng(destinationLatitude, destinationLongitude);
            directionsRequest.Mode = TravelMode.transit;
            directionsRequest.Region = "mx";
            directionsRequest.Avoid = Avoid.none;


            var directionsResponse = new DirectionService().GetResponse(directionsRequest);

            if (directionsResponse.Status != ServiceResponseStatus.Ok)
                log.Error($"Google Directions API returned {directionsResponse.Status} for Origin({originLatitude}, {originLongitude}) & Destination({destinationLatitude}, {destinationLongitude})");
            else if (directionsResponse.Routes.Length == 0)
                log.Warning($"Google Directions API returned 0 results for Origin({originLatitude}, {originLongitude}) & Destination({destinationLatitude}, {destinationLongitude})");
        }
    }
}
