namespace RoutesGoogleMapsScraper.Server
{
    public class HeaderBase
    {
        private readonly string _key;
        private readonly string _value;

        public string Key => _key;
        public string Value => _value;

        public HeaderBase(string key, string value)
        {
            _key = !string.IsNullOrWhiteSpace(key) ? key : string.Empty;
            _value = !string.IsNullOrWhiteSpace(value) ? value : string.Empty;
        }
    }
}
