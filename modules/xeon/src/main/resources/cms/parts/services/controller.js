var thymeleaf = require('/cms/lib/view/thymeleaf');

function handleGet(req) {
    var component = execute('portal.getComponent');

    var params = {
        context: req,
        component: component
    };

    var view = resolve('/cms/view/services.html');
    var body = thymeleaf.render(view, params);

    return {
        body: body,
        contentType: 'text/html'
    };
}

exports.get = handleGet;
