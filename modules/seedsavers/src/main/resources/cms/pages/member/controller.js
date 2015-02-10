var thymeleaf = require('/cms/lib/view/thymeleaf');
var parentPath = './';
var view = resolve(parentPath + 'member.page.html');

function handleGet(req) {

    var editMode = req.mode == 'edit';
    
    var site = execute('portal.getSite');
    var reqContent = execute('portal.getContent');
    var defaultMenu = {
        menu: [false],
        menuName: [reqContent.displayName]
    };

    var menu;
    if (reqContent.x["system:menu-item"]) {
        menu = reqContent.x["system:menu-item"];
    }
    else {
        menu = defaultMenu;
    }


    var params = {
        site: site,
        content: reqContent,
        pageConfig: reqContent.page.config,
        mainRegion: reqContent.page.regions["main"],
        editable: editMode,
        menu: menu
    };
    var body = thymeleaf.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;
