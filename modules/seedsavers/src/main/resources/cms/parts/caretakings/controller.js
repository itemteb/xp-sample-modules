var thymeleaf = require('/cms/lib/view/thymeleaf');

function handleGet(req) {
    var reqContent = execute('portal.getContent');
    var reqComponent = execute('portal.getComponent');
    var view = resolve('./caretakings.html');
    var params = {
        context: req,
        component: reqComponent,
        content: reqContent,
        contentData: reqContent.data
    };
    var body = thymeleaf.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;
