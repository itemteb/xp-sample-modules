var thymeleaf = require('/cms/lib/view/thymeleaf');
var parentPath = './';
var view = resolve(parentPath + 'top-main.page.html');

function handleGet(req) {

    var editMode = req.mode == 'edit';

    var site = execute('portal.getSite');
    var reqContent = execute('portal.getContent');
    var params = {
        context: req,
        site: site,
        reqContent: reqContent,
        topRegion: reqContent.page.regions["top"],
        mainRegion: reqContent.page.regions["main"],
        editable: editMode
    };
    var body = thymeleaf.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;
