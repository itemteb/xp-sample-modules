var thymeleaf = require('view/thymeleaf');



function handleGet(portal) {

    var editMode = portal.request.mode == 'edit';
    var view = resolve('./member.page.html');


    var defaultMenu = {
        menu: [false],
        menuName: [portal.content.displayName]
    };

    var menu;
    if( portal.content.hasMetadata("system:menu") ) {
        menu = portal.content.getMetadata("system:menu").toMap();
    }
    else {
        menu = defaultMenu;
    }


    var params = {
        context: portal,
        site: portal.site,
        content: portal.content,
        pageConfig: portal.content.page.config.toMap(),
        mainRegion: portal.content.page.getRegion("main"),
        editable: editMode,
        menu: menu
    };
    var body = thymeleaf.render(view, params);

    portal.response.contentType = 'text/html';
    portal.response.body = body;
}

exports.get = handleGet;