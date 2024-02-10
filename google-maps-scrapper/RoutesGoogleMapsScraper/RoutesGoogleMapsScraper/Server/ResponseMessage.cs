namespace RoutesGoogleMapsScraper.Server
{
    public class ResponseMessage : ResponseMessageBase
    {
        public ResponseMessage() : base()
        {
            
        }

        public ResponseMessage(bool sucess, string message) : base(sucess, message)
        {
            
        }
    }
}
