package guru.springframework.commands;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CategoryCommand {
    private Long id;
    private String description;
}

// NOTES:
// Command Object is a JavaBean which will be populated with the data from your forms
//
// Think of Command Object as a POJO/JavaBean/etc.. that backs the form in your presentation layer.
//
// Once the form is submitted, all the individual attributes are mapped/bound to this object.
//
// On the way up to presentation, Command Object properties may be used to pre/populate the form.