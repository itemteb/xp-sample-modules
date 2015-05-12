var thymeleaf = require('/cms/lib/view/thymeleaf');
var view = resolve('cities.page.html');

function handleGet(req) {
    var content = execute('portal.getContent');

    var params = {
        content: content
    };
    var body = thymeleaf.render(view, params);

    return {
        contentType: 'text/html',
        body: body

    };
}

exports.get = handleGet;
