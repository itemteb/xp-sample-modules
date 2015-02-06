var thymeleaf = require('/cms/lib/view/thymeleaf');

function handleGet(req) {
    var component = execute('portal.getComponent');
    var slides = component.config ? component.config.slide : [];
    slides = [].concat(slides); // ensure it's an array, even if single element

    var params = {
        context: req,
        component: component,
        slides: slides
    };

    var view = resolve('/cms/view/banner.html');
    var body = thymeleaf.render(view, params);

    return {
        body: body,
        contentType: 'text/html'
    };
}

exports.get = handleGet;
