var citiesLocation = "/features/Cities"


function handlePost(req) {

    var cityName = req.formParams.cityName;
    var cityLocation = req.formParams.cityLocation;

    if (cityName && cityLocation) {
        if (getCity(cityName)) {
            modifyCity(cityName, cityLocation)
        } else {
            createCity(cityName, cityLocation);
        }
    }

    function getCity(cityName) {
        var result = execute('content.get', {
            key: citiesLocation + '/' + cityName
        });
        return result;
    }

    function modifyCity(cityName, cityLocation) {
        var result = execute('content.modify', {
            key: citiesLocation + '/' + cityName,
            editor: function (c) {
                c.data.cityLocation = cityLocation;
                return c;
            }
        });
        return result;
    }

    function createCity(cityName, cityLocation) {
        var result = execute('content.create', {
            name: cityName,
            parentPath: citiesLocation,
            displayName: cityName,
            draft: false,
            requireValid: true,
            contentType: module.name + ':city',
            data: {
                cityLocation: cityLocation
            }
        });

        return result;
    }

    return {
        redirect: execute('portal.pageUrl', {
            path: citiesLocation
        })
    }
}

exports.post = handlePost;