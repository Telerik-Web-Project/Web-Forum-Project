let page = 1;
const paginationFunction = async () => {

    const paginatedPostsUrl = `http://localhost:8080/api/posts/?page=${page}`;
    try {
        const response = await fetch(paginatedPostsUrl);

        if (!response.ok) {
            throw new Error('Failed to fetch posts');
        }

        const posts = await response.json();
        const paginatedPosts = document.getElementById('paginated-posts');

        posts.forEach(post => {
            const postElement = document.createElement('div');
            postElement.textContent = post.title;
            paginatedPosts.appendChild(postElement);
        });

    } catch (error) {
        console.error('Error:', error);
    }
    page++
};

const loadMoreButton = document.getElementById('load-more-btn');
loadMoreButton.addEventListener('click', async () => {
    await paginationFunction();
});