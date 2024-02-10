using AutoMapper;
using RoutesGoogleMapsScraper.DTOs;
using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.Profiles
{
    public class OnTrackBusUserProfile : Profile
    {
        public OnTrackBusUserProfile()
        {
            CreateMap<OnTrackBusUser, OnTrackBusUserReadDTO>()
                .ForMember(dest => dest.Id, opts => opts.MapFrom(src => src.Id))
                .ForMember(dest => dest.Firstname, opts => opts.MapFrom(src => src.Firstname))
                .ForMember(dest => dest.Lastname, opts => opts.MapFrom(src => src.Lastname))
                .ForMember(dest => dest.Email, opts => opts.MapFrom(src => src.Email))
                .ForMember(dest => dest.TrustContact, opts => opts.MapFrom(src => src.TrustContact))
                .ForMember(dest => dest.TopRoutes, opts => opts.MapFrom(src => src.TopRoutes))
                .ForMember(dest => dest.ActivityLog, opts => opts.MapFrom(src => src.ActivityLog))
                .ForMember(dest => dest.SelectedChannels, opts => opts.MapFrom(src => src.SelectedChannels));

            CreateMap<OnTrackBusUserReadDTO, OnTrackBusUser>()
                .ForMember(dest => dest.Id, opts => opts.MapFrom(src => src.Id))
                .ForMember(dest => dest.Firstname, opts => opts.MapFrom(src => src.Firstname))
                .ForMember(dest => dest.Lastname, opts => opts.MapFrom(src => src.Lastname))
                .ForMember(dest => dest.Email, opts => opts.MapFrom(src => src.Email))
                .ForMember(dest => dest.TrustContact, opts => opts.MapFrom(src => src.TrustContact))
                .ForMember(dest => dest.TopRoutes, opts => opts.MapFrom(src => src.TopRoutes))
                .ForMember(dest => dest.ActivityLog, opts => opts.MapFrom(src => src.ActivityLog))
                .ForMember(dest => dest.SelectedChannels, opts => opts.MapFrom(src => src.SelectedChannels));
        }
    }
}
