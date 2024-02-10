using AutoMapper;
using Google.Maps;
using Google.Maps.Direction;
using Newtonsoft.Json;
using RoutesGoogleMapsScraper.APIs;
using RoutesGoogleMapsScraper.Configuration;
using RoutesGoogleMapsScraper.Controllers;
using RoutesGoogleMapsScraper.Models;
using RoutesGoogleMapsScraper.Server;
using Serilog;
using Serilog.Sinks.SystemConsole.Themes;
using System.Diagnostics;

namespace RoutesGoogleMapsScraper
{
    public class Program
    {
        public static void Main(string[] args)
        {
            #region Server environment variables
            var schema = Environment.GetEnvironmentVariable("APP_SCHEMA") ?? "http";
            var base_url = Environment.GetEnvironmentVariable("APP_BASE_URL") ?? "localhost";
            var port = Environment.GetEnvironmentVariable("PORT") ?? "9091";
            OnTrackBusController.hostname = Environment.GetEnvironmentVariable("ONTRACKBUS_HOSTNAME") ?? "ontrackbus.com";
            //var googleMapsAPIKey = Environment.GetEnvironmentVariable("GOOGLE_MAPS_API_KEY") ?? string.Empty;
            #endregion

            #region Serilog Configuration
            Log.Logger = new LoggerConfiguration()
                .MinimumLevel.Debug()
                .WriteTo.Console(theme: SystemConsoleTheme.Literate)
                .CreateLogger();

            ILogger logger = Log.Logger;
            #endregion

            #region AutoMapper Configuration
            logger.Information("AutoMapper configuration process started");
            var autoMapperConfig = new AutoMapperStartupConfiguration();
            IMapper mapper = autoMapperConfig.Configuration.CreateMapper();
            logger.Information("AutoMapper configuration process finished");
            #endregion

            logger.Information("Starting server...");

            WebServer webServer = new WebServer($"{schema}://{base_url}:{port}/");
            try
            {
                webServer.Start(logger, mapper);
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                webServer.Stop();
            }

            //GoogleSigned.AssignAllServices(new GoogleSigned());

            //var directionsRequest = new DirectionRequest();
            //double originLatitude, originLongitude, destinationLatitude, destinationLongitude;
            //originLatitude = 20.63327;
            //originLongitude = -103.23679;
            //destinationLatitude = 20.6285;
            //destinationLongitude = -103.24276;
            //directionsRequest.Origin = new LatLng(originLatitude, originLongitude);
            //directionsRequest.Destination = new LatLng(destinationLatitude, destinationLongitude);
            //directionsRequest.Mode = TravelMode.transit;
            //directionsRequest.Region = "mx";
            //directionsRequest.Avoid = Avoid.none;


            //var directionsResponse = new DirectionService().GetResponse(directionsRequest);

            //if (directionsResponse.Status != ServiceResponseStatus.Ok)
            //    logger.Error($"Google Directions API returned {directionsResponse.Status} for Origin({originLatitude}, {originLongitude}) & Destination({destinationLatitude}, {destinationLongitude})");
            //else if (directionsResponse.Routes.Length == 0)
            //    logger.Warning($"Google Directions API returned 0 results for Origin({originLatitude}, {originLongitude}) & Destination({destinationLatitude}, {destinationLongitude})");
        }
    }
}
