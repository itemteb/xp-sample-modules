var xslt = require('view/xslt');
var view = resolve('/view/trampoline-image.xsl');

function handleGet(portal) {

    var xml = <dummy/>;
    var editMode = portal.request.mode == 'edit';
    var params = {
        editable: editMode,
        title: portal.content.displayName,
        componentType: portal.component.type,
        imageUrl: portal.component.image != null ? portal.url.createImageByIdUrl(portal.component.image) : null
    };

    var body = system.xslt.render(view, xml, params);

    portal.response.contentType = 'text/html';
    portal.response.body = body;

}

exports.get = handleGet;