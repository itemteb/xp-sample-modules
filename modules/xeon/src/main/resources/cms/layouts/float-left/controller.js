exports.get = function (req) {
    var component = execute('portal.getComponent');

    return {
        body: execute('thymeleaf.render', {
            view: resolve('float-left.html'),
            model: {
                leftRegion: component.regions["left"],
                mainRegion: component.regions["main"]
            }
        })
    };

};
