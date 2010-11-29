

<%@ attribute name="property" required="true" description="The name element for the html input." %>
<%@ attribute name="src" required="true" description="" %>
<%@ attribute name="title" required="false" description="" %>
<%@ attribute name="alt" required="true" description="" %>
<%@ attribute name="tabindex" required="false" description="" %>
<%@ attribute name="styleClass" required="true" description="" %>
<%@ attribute name="onclick" required="true" description="" %>

<input type="image"
    name="${property}"
    src="${src}"
    title="${title}"
    alt="${alt}"
    tabindex="${tabindex}"
    class="${styleClass}"
    onclick="${onclick}"
/>