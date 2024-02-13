namespace RoutesGoogleMapsScraper.Server
{
    public class ResponseMessage : ResponseMessageBase
    {
        public ResponseMessage() : base()
        {
            
        }

        public ResponseMessage(bool success, string message) : base(success, message)
        {
            
        }
    }
}
