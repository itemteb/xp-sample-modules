var thymeleaf = require('/cms/lib/view/thymeleaf');
var view = resolve('city-creation.page.html');
var service = require('service.js').service;
var citiesLocation = "/features/Cities"

function handleGet(req) {
    var cityServiceUrl = service.serviceUrl('city');

    var cityName;
    var cityLocation;
    if (req.params.city) {
        var city = getCity(req.params.city);
        if (city) {
            cityName = city.displayName;
            cityLocation = city.data.cityLocation;
        }
    }

    cityName = cityName || "City Name";
    cityLocation = cityLocation || "lat,lon";

    var params = {
        cityServiceUrl: cityServiceUrl,
        defaultCityName: cityName,
        defaultCityLocation: cityLocation
    };
    var body = thymeleaf.render(view, params);

    function getCity(cityName) {
        var result = execute('content.get', {
            key: citiesLocation + '/' + cityName
        });
        return result;
    }

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;