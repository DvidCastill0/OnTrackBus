namespace RoutesGoogleMapsScraper.Server
{
    public class ResponseMessageBase
    {
        public bool success { get; set; }
        public string message { get; set; }

        public ResponseMessageBase()
        {
            success = true;
            message = string.Empty;
        }

        public ResponseMessageBase(bool success, string message)
        {
            this.success = success;
            this.message = message;
        }
    }
    
}
