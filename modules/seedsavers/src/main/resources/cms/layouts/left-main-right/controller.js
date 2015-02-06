var thymeleaf = require('/cms/lib/view/thymeleaf');

function handleGet(req) {

    var editMode = req.mode == 'edit';
    var component = execute('portal.getComponent');
    var view = resolve('./left-main-right.html');

    var params = {
        editable: editMode,
        component: component,
        leftRegion: component.regions["left"],
        mainRegion: component.regions["main"],
        rightRegion: component.regions["right"]
    };

    var body = thymeleaf.render(view, params);

    return {
        body: body,
        contentType: 'text/html',
        status: 200
    };
}

exports.get = handleGet;
