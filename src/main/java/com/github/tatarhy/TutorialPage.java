package com.github.tatarhy;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.io.IClusterable;
import org.pegdown.PegDownProcessor;

import java.util.ArrayList;
import java.util.List;

public class TutorialPage extends WebPage {
    private final List<Comment> comments = new ArrayList<>();

    public static class Comment implements IClusterable {
        private static final long serialVersionUID = 1L;

        private String text;
        private String author;

        /**
         * Constructor.
         */
        public Comment() {
        }

        /**
         * Copy constructor.
         *
         * @param item the item to copy the values from.
         */
        public Comment(Comment item) {
            text = item.text;
            author = item.author;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }

    public static class CommentListView extends PropertyListView<Comment> {
        public CommentListView(String id, List<Comment> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(ListItem<Comment> item) {
            PegDownProcessor processor = new PegDownProcessor();
            String text = processor.markdownToHtml(item.getModelObject().getText());
            item.add(new Label("text", text).setEscapeModelStrings(false));
            item.add(new Label("author"));
        }
    }

    public static abstract class CommentForm extends StatelessForm<Comment> {
        public CommentForm(String id) {
            super(id, new CompoundPropertyModel<>(new Comment()));
            setOutputMarkupId(true);
            add(new TextField<>("author"));
            add(new TextField<>("text"));
            add(new AjaxFallbackButton("post", this) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    super.onSubmit(target, form);
                    CommentForm.this.onSubmit(target);

                    // reset the model
                    Comment comment = CommentForm.this.getModelObject();
                    comment.setText("");
                    comment.setAuthor("");
                }
            });
        }

        protected abstract void onSubmit(AjaxRequestTarget target);
    }

    public class CommentBox extends WebMarkupContainer {
        public CommentBox(String id) {
            super(id);
            // let wicket generate a markup-id so the contents can be
            // updated through an AJAX call.
            setOutputMarkupId(true);

            add(new CommentListView("commentList", comments));
            add(new CommentForm("commentForm") {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    Comment comment = getModelObject();
                    onCommentSubmit(comment, target);
                }
            });
        }

        void onCommentSubmit(Comment comment, AjaxRequestTarget target) {
            // add the item
            comments.add(new Comment(comment));
            // repaint our panel
            target.add(this);
        }
    }

    public TutorialPage(PageParameters parameters) {
        super(parameters);

        setStatelessHint(true);
        setVersioned(false);
        add(new CommentBox("commentBox"));
    }
}
