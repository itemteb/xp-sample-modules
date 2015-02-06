var thymeleaf = require('/cms/lib/view/thymeleaf');
var contentService = require('/cms/lib/contentService');

function handleGet() {

    var site = execute('portal.getSite');
    var reqComponent = execute('portal.getComponent');
    var maxCount = 100;
    if (reqComponent.config.maxCount) {
        maxCount = reqComponent.config.maxCount;
    }

    var membersResult = contentService.getChildren(site._path + "/members", maxCount);
    var membersContent = membersResult.contents;
    var membersTotal = membersResult.total;

    var params = {
        component: reqComponent,
        maxCount: maxCount,
        members: membersContent,
        membersTotal: membersTotal
    };

    var view = resolve('./member-list.html');
    var body = thymeleaf.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;
