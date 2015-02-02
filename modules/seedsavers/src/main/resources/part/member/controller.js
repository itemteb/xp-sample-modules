var thymeleaf = require('/lib/view/thymeleaf');

var defaultMemberAddress = [
    {
        street: "123 Street",
        postalCode: "Postal code",
        postalPlace: "Postal place"
    }
];

var defaultMember = {
    name: "Member name",
    address: defaultMemberAddress
};


function handleGet() {

    var reqContent = execute('portal.getContent');
    var reqComponent = execute('portal.getComponent');
    var member;
    if (reqContent.type == "portal:page-template") {
        member = defaultMember;
    }
    else {
        member = reqContent.data;
        member.address = [].concat(member.address);
    }

    var params = {
        component: reqComponent,
        config: reqComponent.config,
        content: reqContent,
        member: member
    };

    var view = resolve('./member.html');
    var body = thymeleaf.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;
