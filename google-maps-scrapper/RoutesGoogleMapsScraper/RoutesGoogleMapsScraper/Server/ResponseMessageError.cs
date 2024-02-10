namespace RoutesGoogleMapsScraper.Server
{
    public class ResponseMessageError : ResponseMessageBase
    {
        public ResponseMessageError() : base() { }
        public ResponseMessageError(bool success, string message) : base(success, message)
        {
            
        }
    }
}
