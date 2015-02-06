var thymeleaf = require('/cms/lib/view/thymeleaf');
var parentPath = './';
var view = resolve(parentPath + 'family.page.html');

function handleGet(req) {

    var editMode = req.mode == 'edit';
    var site = execute('portal.getSite');
    var reqContent = execute('portal.getContent');
    
    var model = {
        context: req,
        site: site,
        content: reqContent,
        mainRegion: reqContent.page.regions["main"],
        editable: editMode
    };
    var body = thymeleaf.render(view, model);

    return {
        contentType: 'text/html',
        body: body
    };

}

exports.get = handleGet;
