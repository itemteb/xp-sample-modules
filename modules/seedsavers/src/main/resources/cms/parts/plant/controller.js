var thymeleaf = require('/cms/lib/view/thymeleaf');

function handleGet(req) {

    var reqContent = execute('portal.getContent');
    var reqComponent = execute('portal.getComponent');
    var view = resolve('./plant.html');
    var plant;
    if (reqContent.type == "portal:page-template") {
        plant = {
            binomialName: "Binomial name",
            norwegianNames: Java.to(["navn1", "navn2", "navn3"], "java.lang.String[]")
        };
    }
    else {
        plant = {
            binomialName: reqContent.data.binomialName ? reqContent.data.binomialName[0] : "",
            norwegianNames: reqContent.data.norwegianNames
        };
    }
    var params = {
        component: reqComponent,
        content: reqContent,
        plant: plant
    };
    var body = thymeleaf.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;
