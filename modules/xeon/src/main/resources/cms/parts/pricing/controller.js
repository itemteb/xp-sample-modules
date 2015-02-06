var thymeleaf = require('/cms/lib/view/thymeleaf');

function handleGet(req) {
    var component = execute('portal.getComponent');

    var slides = component.config['slide'] || [];


    var params = {
        context: req,
        component: component,
        slides: slides
    };

    var view = resolve('/cms/view/pricing.html');
    var body = thymeleaf.render(view, params);

    return {
        body: body,
        contentType: 'text/html'
    };
}

exports.get = handleGet;
