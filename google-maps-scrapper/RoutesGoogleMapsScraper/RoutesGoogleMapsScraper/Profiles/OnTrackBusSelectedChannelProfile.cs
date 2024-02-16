using AutoMapper;
using RoutesGoogleMapsScraper.DTOs;
using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.Profiles
{
    public class OnTrackBusSelectedChannelProfile : Profile
    {
        public OnTrackBusSelectedChannelProfile()
        {
            CreateMap<OnTrackBusSelectedChannel, OnTrackBusSelectedChannelReadDTO>()
                .ForMember(dest => dest.Id, opts => opts.MapFrom(src => src.Id))
                .ForMember(dest => dest.Name, opts => opts.MapFrom(src => src.Name));

            CreateMap<OnTrackBusSelectedChannelReadDTO, OnTrackBusSelectedChannel>()
                .ForMember(dest => dest.Id, opts => opts.MapFrom(src => src.Id))
                .ForMember(dest => dest.Name, opts => opts.MapFrom(src => src.Name));
        }
    }
}
