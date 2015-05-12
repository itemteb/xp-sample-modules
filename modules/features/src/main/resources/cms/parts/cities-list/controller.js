var thymeleaf = require('/cms/lib/view/thymeleaf');
var view = resolve('cities-list.page.html');
var service = require('service.js').service;
var citiesLocation = "/features/Cities"

function handleGet(req) {

    var currentCityName;
    var cities;

    if (req.params.city) {
        var city = getCity(req.params.city);
        if (city) {
            currentCityName = city.displayName;
            cities = execute('content.query', {
                    start: 0,
                    count: 20,
                    contentTypes: [
                        module.name + ':city'
                    ],
                    "sort": "geoDistance('data.cityLocation','" + city.data.cityLocation + "')",
                    "query": "_name != '" + currentCityName + "'"
                }
            );
        }
    }

    if (!currentCityName) {
        currentCityName = "Select";
    }

    if (!cities) {
        cities = execute('content.query', {
                start: 0,
                count: 20,
                contentTypes: [
                    module.name + ':city'
                ]
            }
        );
    }

    var content = execute('portal.getContent');
    var currentPage = execute('portal.pageUrl', {
        path: content._path
    });

    var params = {
        cities: cities.contents,
        currentCity: currentCityName,
        currentPage: currentPage
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