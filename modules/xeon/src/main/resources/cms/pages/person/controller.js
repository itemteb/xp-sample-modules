var xeon = require('/cms/lib/xeon');

function handleGet(req) {
    var params = xeon.defaultParams(req);

    var body = execute('thymeleaf.render', {
        view: resolve('/cms/view/page.html'),
        model: params
    });

    return {
        body: body,
        contentType: 'text/html'
    };
}

exports.get = handleGet;
