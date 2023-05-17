<table>
    <thead>
    <tr>
        <th>序号</th>
        <th>网站</th>
        <th>语言</th>
        <th>标签</th>
        <th>报告失效时间</th>
        <th>失效原因</th>
    </tr>
    </thead>
    <tbody>
    <#list websites as website>
        <tr>
            <td>${website_index + 1}</td>
            <td>${website.title!""} <a href="${website.url!""}" target="_blank">${website.url!""}</a>
                <br> ${website.description!""}</td>
            <td>${website.lang!""}</td>
            <td>
                <#if website.features??>
                    <#list website.features as feature>
                        ${feature.label!""}
                    </#list>
                </#if>
            </td>
            <td>${website.reportedInvalidDate!""}</td>
            <td>${website.reportedInvalidReason!""}</td>
        </tr>
    </#list>
    </tbody>
</table>
