var thymeleaf = require('view/thymeleaf');

function handleGet(portal) {

    var editMode = portal.request.mode == 'edit';
    var view = resolve('./plant.page.html');
    var params = {
        context: portal,
        site: portal.site,
        content: portal.content,
        mainRegion: portal.content.page.getRegion("main"),
        editable: editMode,
        from: "plant"
    };
    var body = thymeleaf.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}


exports.get = handleGet;